/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp.
 * 825 Sylk BLDG., 1-Yamashita-Cho, Naka-Ku, Kanagawa-Ken, Yokohama-City, JAPAN.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA.
 * 
 * (R)OpenDolphin version 2.4, Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp. 
 * (R)OpenDolphin comes with ABSOLUTELY NO WARRANTY; for details see the GNU General 
 * Public License, version 3 (GPLv3) This is free software, and you are welcome to redistribute 
 * it under certain conditions; see the GPLv3 for details.
 */
package open.dolphin.delegater;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.sun.jersey.api.client.ClientResponse;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitList;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.PostSchedule;
import open.dolphin.util.BeanUtils;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * PVT 関連の Business Delegater　クラス (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ScheduleDelegater extends BusinessDelegater {

    private static final String RES_SCHEDULE = "/schedule";

    private static final boolean debug = false;
    private static final ScheduleDelegater instance;

    static {
        instance = new ScheduleDelegater();
    }

    public static ScheduleDelegater getInstance() {
        return instance;
    }

    private ScheduleDelegater() {
    }

    public int removePvt(long pvtPK, long ptPK, String startDate) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_SCHEDULE).append("/pvt/");
        sb.append(pvtPK).append(",").append(ptPK).append(",").append(startDate);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // Check
        checkStatus(response);

        // Count
        return 1;
    }

    public List<PatientVisitModel> getPvtList(String pvtDate) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_SCHEDULE).append("/pvt/").append(pvtDate);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientVisitList result = mapper.readValue(br, PatientVisitList.class);

        // Decode
        List<PatientVisitModel> list = result.getList();
        if (list != null && list.size() > 0) {
            for (PatientVisitModel pm : list) {
                decodeHealthInsurance(pm.getPatientModel());
                if (pm.getFirstInsurance() == null && pm.getPatientModel().getPvtHealthInsurances() != null) {
                    PVTHealthInsuranceModel h = pm.getPatientModel().getPvtHealthInsurances().get(0);
                    pm.setFirstInsurance(h.toString());
                    Log.outputFuncLog(Log.LOG_LEVEL_0, "I", "PvtList", h.toString());
                }
            }
        }
        return list;
    }

    public List<PatientVisitModel> getAssingedPvtList(String pvtDate, String orcaId, String unassignedId) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append(RES_SCHEDULE).append("/pvt/");
        sb.append(orcaId).append(",");
        sb.append(unassignedId).append(",");
        sb.append(pvtDate);
        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientVisitList result = mapper.readValue(br, PatientVisitList.class);

        // Decode
        List<PatientVisitModel> list = result.getList();
        if (list != null && list.size() > 0) {
            for (PatientVisitModel pm : list) {
                decodeHealthInsurance(pm.getPatientModel());
                if (pm.getFirstInsurance() == null && pm.getPatientModel().getPvtHealthInsurances() != null) {
                    PVTHealthInsuranceModel h = pm.getPatientModel().getPvtHealthInsurances().get(0);
                    pm.setFirstInsurance(h.toString());
                    Log.outputFuncLog(Log.LOG_LEVEL_0, "I", "PvtList", h.toString());
                }
            }
        }
        return list;
    }

    public int postSchedule(PostSchedule ps) throws Exception {

        // PATH
        String path = "/schedule/document";
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);
        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(ps);
        byte[] data = json.getBytes(UTF8);

        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }

    /**
     * バイナリの健康保険データをオブジェクトにデコードする。
     *
     * @param patient 患者モデル
     */
    private void decodeHealthInsurance(PatientModel patient) {

        // Health Insurance を変換をする beanXML2PVT
        Collection<HealthInsuranceModel> c = patient.getHealthInsurances();

        if (c != null && c.size() > 0) {

            List<PVTHealthInsuranceModel> list = new ArrayList(c.size());

            for (HealthInsuranceModel model : c) {
                try {
                    // byte[] を XMLDecord
                    PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel) BeanUtils.xmlDecode(model.getBeanBytes());
                    list.add(hModel);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }

            patient.setPvtHealthInsurances(list);
            patient.getHealthInsurances().clear();
            patient.setHealthInsurances(null);
        }
    }

    @Override
    protected void debug(int status, String entity) {
        if (debug || DEBUG) {
            super.debug(status, entity);
        }
    }
}
