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
package open.dolphin.dto;

import java.util.Date;


/**
 * ModuleSearchSpec
 * 
 * @author Minagawa,Kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ModuleSearchSpec extends DolphinDTO {
	
	private static final long serialVersionUID = 4550131751936543011L;

	public static final int ENTITY_SEARCH 		= 0;
	
	private int code;
	
	private long karteId;
	
	private String patientId;
	
	private String entity;
	
	private Date[] fromDate;
	
	private Date[] toDate;
	
	private String status;
	

	/**
	 * @param code The code to set.
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return Returns the code.
	 */
	public int getCode() {
		return code;
	}
	

	public long getKarteId() {
		return karteId;
	}

	public void setKarteId(long karteId) {
		this.karteId = karteId;
	}

	/**
	 * @param patientId The patientId to set.
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return Returns the patientId.
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * @param fromDate The fromDate to set.
	 */
	public void setFromDate(Date[] fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return Returns the fromDate.
	 */
	public Date[] getFromDate() {
		return fromDate;
	}

	/**
	 * @param toDate The toDate to set.
	 */
	public void setToDate(Date[] toDate) {
		this.toDate = toDate;
	}

	/**
	 * @return Returns the toDate.
	 */
	public Date[] getToDate() {
		return toDate;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param entity The entity to set.
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return Returns the entity.
	 */
	public String getEntity() {
		return entity;
	}
}
