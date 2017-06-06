package net.ahm.careengine.dao.integration;

import net.ahm.careengine.dao.BaseDAO;

import org.apache.log4j.Logger;

public class TestDAO extends BaseDAO {
	private static Logger logger = Logger.getLogger(TestDAO.class);
	
	public void runQuery(String sql){
		try {
			logger.debug("Running sql = " + sql);
			logger.debug(getJdbcTemplate());
			getJdbcTemplate().execute(sql);
		} catch (Exception e) {
			logger.error (e.getMessage(), e);
		}
	}
	
	public boolean exists(String tableNm, String colNm, String value ){
		try {			
			String sql = "SELECT COUNT(*) FROM " + tableNm + " WHERE " + colNm +  " = " + value; 
			logger.debug("Running sql = " + sql);
			int count = getJdbcTemplate().queryForInt(sql);
			logger.debug("Returning = " + (count > 0?true:false));
			return count > 0?true:false;
		}catch (Exception e) {
			logger.error (e.getMessage(), e);
		}
		return true;
	}

    public int getCount(String sql) throws Exception {
        logger.debug("Running sql = " + sql);
        logger.debug(getJdbcTemplate());
        return getJdbcTemplate().queryForInt(sql);
    }
		
}
