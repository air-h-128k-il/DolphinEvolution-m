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
package open.dolphin.impl.labrcv;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.client.LabResultParser;
import open.dolphin.infomodel.NLaboItem;
import open.dolphin.infomodel.NLaboModule;

/**
 * NLabParser
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class NLabParser implements LabResultParser {

    private String encoding = "SHIFT-JIS";

    private boolean DEBUG;

    public NLabParser() {
    }

    /**
     * 入力ストリームの検査結果をパースする。
     *
     * @param path
     * @return
     * @throws java.io.IOException
     */
    @Override
    public List<NLaboImportSummary> parse(Path path) throws IOException {
        //public List<NLaboImportSummary> parse(File labFile) throws IOException {

        String line;
        String curKey = null;
        NLaboModule curModule = null;
        List<NLaboModule> allModules = new ArrayList<>();
        List<NLaboImportSummary> retList = new ArrayList<>();

        SimpleDateFormat defaultDF = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        SimpleDateFormat df8 = new SimpleDateFormat(DATE_FORMAT_8);
        SimpleDateFormat df10 = new SimpleDateFormat(DATE_FORMAT_10);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path), getEncoding()))) {
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {

                lineNo++;

                line = line.replaceAll("\"", "");

                if (line.equals("") || line.length() == 0) {
                    continue;
                }

                String[] data = line.split(CSV_DELIM);    // CSV

                if (data.length == 0) {
                    continue;
                }

                String labCode = data[0];       // Lab code
                String patientId = data[1];     // 患者ID
                String sampleDate = data[2];    // 検体採取日（受付日）
                String patientName = data[3];   // 患者氏名
                String patientSex = data[4];    // 患者性別
                int baseIndex = 5;

                if (DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("------------------------------------------").append("\n");
                    sb.append(lineNo).append(" ");
                    sb.append(labCode).append(" ");
                    sb.append(patientId).append(" ");
                    sb.append(sampleDate).append(" ");
                    sb.append(patientName).append(" ");
                    sb.append(patientSex);
                    System.err.println(sb.toString());
                }

                sampleDate = sampleDate.replaceAll("/", "-");
                Date date = null;

                // 検体採取日、時刻
                try {
                    if (sampleDate.length() == 8) {
                        // yyyyMMdd
                        date = df8.parse(sampleDate);
                    } else if (sampleDate.length() == 10) {
                        // yyyy-MM-dd
                        date = df10.parse(sampleDate);

                    } else if (sampleDate.length() == 16) {
                        // yyyy-MM-dd HH:mm
                        date = defaultDF.parse(sampleDate);
                    }

                    // yyyy-MM-dd HH:mm
                    sampleDate = defaultDF.format(date);

                } catch (Exception e) {
                    ClientContext.getLaboTestLogger().warn(e);
                    throw new IOException(e.getMessage());
                }

                //---------------------------------------------
                // 検査箋（検査モジュール）のキー
                //  = patientId.sampleDate.labCode
                //---------------------------------------------
                String testKey;
                StringBuilder buf = new StringBuilder();
                buf.append(patientId);
                buf.append(".");
                buf.append(sampleDate);
                buf.append(".");
                buf.append(labCode);
                testKey = buf.toString();

                if (!testKey.equals(curKey)) {
                    // 新しい current module を生成する
                    curKey = testKey;
                    curModule = new NLaboModule();
                    curModule.setLaboCenterCode(labCode);
                    curModule.setPatientId(patientId);
                    curModule.setPatientName(patientName);
                    curModule.setPatientSex(patientSex);
                    curModule.setSampleDate(sampleDate);
                    curModule.setModuleKey(curKey);         // Key
                    allModules.add(curModule);
                }

                NLaboItem item = new NLaboItem();

                item.setPatientId(patientId);   // カルテ番号
                item.setSampleDate(sampleDate); // 検体採取日
                item.setLaboCode(labCode);

                int index = 0;
                while (true) {

                    if ((baseIndex + index) >= data.length) {
                        break;
                    }

                    String val = data[baseIndex + index];

                    if (val != null) {
                        val = val.trim();
                    }

                    if (val == null || val.equals("")) {
                        index++;
                        continue;
                    }

                    switch (index) {

                        case 0:
                            debug("乳ビ", val);
                            item.setLipemia(val);       // 乳ビ
                            break;

                        case 1:
                            debug("溶血", val);
                            item.setHemolysis(val);     // 溶血
                            break;

                        case 2:
                            debug("透析", val);
                            item.setDialysis(val);      // 透析
                            break;

                        case 3:
                            debug("報告状況", val);
                            item.setReportStatus(val);  // 報告状況
                            break;

                        case 4:
                            debug("グループコード", val);
                            item.setGroupCode(val);     // グループコード
                            break;

                        case 5:
                            debug("グループ名称", val);
                            item.setGroupName(val);     // グループ名称
                            break;

                        case 6:
                            debug("検査項目コード・親", val);
                            item.setParentCode(val);    // 検査項目コード・親
                            break;

                        case 7:
                            debug("検査項目コード", val);
                            item.setItemCode(val);      // 検査項目コード
                            break;

                        case 8:
                            debug("MEDISコード", val);
                            item.setMedisCode(val);     // MEDIS コード
                            break;

                        case 9:
                            debug("検査項目名", val);
                            item.setItemName(val);      // 検査項目名
                            break;

                        case 10:
                            debug("異常区分", val);
                            item.setAbnormalFlg(val);   // 異常区分
                            break;

                        case 11:
                            debug("基準値", val);
                            item.setNormalValue(val);   // 基準値
                            break;

                        case 12:
                            debug("検査結果", val);
                            item.setValue(val);         // 検査結果
                            break;

                        case 13:
                            debug("単位", val);
                            item.setUnit(val);          // 単位
                            break;

                        case 14:
                            debug("検体材料コード", val);
                            item.setSpecimenCode(val);   // 検体材料コード
                            break;

                        case 15:
                            debug("検体材料名称", val);
                            item.setSpecimenName(val);   // 検体材料名称
                            break;

                        case 16:
                            debug("コメントコード1", val);
                            item.setCommentCode1(val);  // コメントコード1
                            break;

                        case 17:
                            debug("コメント1", val);
                            item.setComment1(val);      // コメント1
                            break;

                        case 18:
                            debug("コメントコード2", val);
                            item.setCommentCode2(val);  // コメントコード2
                            break;

                        case 19:
                            debug("コメント2", val);
                            item.setComment2(val);     // コメント2
                            break;
                    }

                    index++;
                }

                // 検査結果値がない場合
                if (item.getValue() == null || item.getValue().equals("")) {

                    String resultValue = MIHOKOKU;

                    // 三菱化学メディエンスの場合
                    if (labCode.equals("M")) {
                        resultValue = NO_RESULT;
                    }

                    item.setValue(resultValue);
                }

                // Group Code, Name がない場合 Lab code を設定する
                if (item.getGroupCode() == null || item.getGroupCode().equals("")) {
                    //System.err.println("item.getGroupCode()==null");
                    item.setGroupCode(item.getLaboCode());
                    item.setGroupName(item.getLaboCode());
                }
                //System.err.println(item.getGroupCode());

                // 親コードがない場合 item code を設定する
                if (item.getParentCode() == null || item.getParentCode().equals("")) {
                    item.setParentCode(item.getItemCode());
                }

                // 関係を構築する
                curModule.addItem(item);
                item.setLaboModule(curModule);
            }
        }

        // サマリを生成する
        for (NLaboModule module : allModules) {
            NLaboImportSummary summary = new NLaboImportSummary();
            summary.setLaboCode(module.getLaboCenterCode());
            summary.setPatientId(module.getPatientId());
            summary.setPatientName(module.getPatientName());
            summary.setPatientSex(module.getPatientSex());
            summary.setSampleDate(module.getSampleDate());
            summary.setNumOfTestItems(String.valueOf(module.getItems().size()));
            summary.setModule(module);
            retList.add(summary);
        }

        return retList;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    private void debug(String item, String value) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append(item);
            sb.append("=");
            sb.append(value);
            System.out.println(sb.toString());
        }
    }
}
