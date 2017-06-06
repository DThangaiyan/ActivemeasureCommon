/*-----------------------------------------------------------
Copyright (C) 2001 SAS Institute, Inc. All rights reserved.
 
Notice:
 The following permissions are granted provided that the
 above copyright and this notice appear in the code and
 any related documentation. Permission to copy, modify and
 distribute code generated using or distributed with
 SAS Enterprise Miner J*Score software and any executables
 derived from such source code is limited to customers of
 SAS Institute with a valid license for SAS Enterprise Miner
 J*Score software. Any distribution of such code in any form
 shall be on an "AS IS" basis without warranty of any kind.
 SAS and all other SAS Institute Inc. product and service
 names are registered trademarks or trademarks of SAS 
 Institute Inc. in the USA and other countries. Except as 
 contained in this notice, the name of SAS Institute, SAS 
 Enterprise Miner and SAS Enterprise Miner J*Score software 
 shall not be used in the advertising or promotion of 
 products or services without prior written authorization 
 from SAS Institute Inc.
------------------------------------------------------------*/
 
package eminer.user.model_0915;
import java.util.Map;
import java.util.HashMap;
import com.sas.ds.*;
import com.sas.analytics.eminer.jscore.util.*;
 
 
/** 
* The Score class encapsulates data step scoring code generated
* by the SAS Enterprise Miner Java Scoring Component.
*                                                              
* @since 1.0
* @version Jscore 1.2
* @author SAS Enterprise Miner Java Scoring Component
* @see com.sas.analytics.eminer.jscore.util.Jscore
*/ 
 
public class Score
       implements Jscore {
   private boolean dataModified;
   public boolean reuseOutputMap;
   private DS dscode;
 
/** 
* A map of the (key) name, (value) reference pair for every 
* variable modified by the score method.
* This is provided primarily for optional advanced development.
*/ 
   protected Map outputVariables;
 
   public Score() {
      DSFormats formatLib = new JscoreUserFormats();
      dataModified = false;
      reuseOutputMap = false;
      dscode = new DS(formatLib);
   }
 
 
/** 
* Executes the scoring code and returns an output Map.
* By default a new instance of the output map is allocated
* the method is invoked. To modify this behavior set the public
* field reuseOutputMap to true. This will cause only one
* output map to be allocated, subsequent calls will reuse the
* same map. Note this will cause the content of the map to be
* over written at each time this method is invoked.
* @param a reference to a Map object that contains the
* (key) name String, (value) pair, for the
* input variables to be used in the "scoring" code.
* @return a Map of the (key) name, (value) reference pair for all 
* variables modified in the "scoring" code.
* @throws com.sas.analytics.eminer.jscore.util.JscoreException Invalid input data type for String \"variableName\".
* @throws com.sas.analytics.eminer.jscore.util.JscoreException Invalid input data type for Double \"variableName\".
*/ 
   public Map score ( Map indata) throws JscoreException {
      Object tmpVar;
 
      dscode.initialize();
      if ( reuseOutputMap == false || outputVariables == null)
           outputVariables = new HashMap( 28, .75F );
 
      tmpVar = indata.get("CHRONIC_AMI");
      if( tmpVar != null) {
         try {
             dscode.CHRONIC_AMI = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"CHRONIC_AMI\".");
         }
      }
      else 
         dscode.CHRONIC_AMI = Double.NaN;
 
      tmpVar = indata.get("CNT_ME");
      if( tmpVar != null) {
         try {
             dscode.CNT_ME = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"CNT_ME\".");
         }
      }
      else 
         dscode.CNT_ME = Double.NaN;
 
      tmpVar = indata.get("CNT_MK");
      if( tmpVar != null) {
         try {
             dscode.CNT_MK = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"CNT_MK\".");
         }
      }
      else 
         dscode.CNT_MK = Double.NaN;
 
      tmpVar = indata.get("DIAG_RATE_PRE12");
      if( tmpVar != null) {
         try {
             dscode.DIAG_RATE_PRE12 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"DIAG_RATE_PRE12\".");
         }
      }
      else 
         dscode.DIAG_RATE_PRE12 = Double.NaN;
 
      tmpVar = indata.get("DIAG_RATE_PRE3");
      if( tmpVar != null) {
         try {
             dscode.DIAG_RATE_PRE3 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"DIAG_RATE_PRE3\".");
         }
      }
      else 
         dscode.DIAG_RATE_PRE3 = Double.NaN;
 
      tmpVar = indata.get("ER_RATE_PRE12");
      if( tmpVar != null) {
         try {
             dscode.ER_RATE_PRE12 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ER_RATE_PRE12\".");
         }
      }
      else 
         dscode.ER_RATE_PRE12 = Double.NaN;
 
      tmpVar = indata.get("ER_RATE_PRE3");
      if( tmpVar != null) {
         try {
             dscode.ER_RATE_PRE3 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ER_RATE_PRE3\".");
         }
      }
      else 
         dscode.ER_RATE_PRE3 = Double.NaN;
 
      tmpVar = indata.get("GROUP_CHOLES");
      if( tmpVar != null) {
         try {
             dscode.GROUP_CHOLES = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"GROUP_CHOLES\".");
         }
      }
      else 
         dscode.GROUP_CHOLES = Double.NaN;
 
      tmpVar = indata.get("HOSP_RATE_PRE12");
      if( tmpVar != null) {
         try {
             dscode.HOSP_RATE_PRE12 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"HOSP_RATE_PRE12\".");
         }
      }
      else 
         dscode.HOSP_RATE_PRE12 = Double.NaN;
 
      tmpVar = indata.get("HOSP_RATE_PRE3");
      if( tmpVar != null) {
         try {
             dscode.HOSP_RATE_PRE3 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"HOSP_RATE_PRE3\".");
         }
      }
      else 
         dscode.HOSP_RATE_PRE3 = Double.NaN;
 
      tmpVar = indata.get("ICU_RATE_PRE12");
      if( tmpVar != null) {
         try {
             dscode.ICU_RATE_PRE12 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ICU_RATE_PRE12\".");
         }
      }
      else 
         dscode.ICU_RATE_PRE12 = Double.NaN;
 
      tmpVar = indata.get("ICU_RATE_PRE3");
      if( tmpVar != null) {
         try {
             dscode.ICU_RATE_PRE3 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ICU_RATE_PRE3\".");
         }
      }
      else 
         dscode.ICU_RATE_PRE3 = Double.NaN;
 
      tmpVar = indata.get("LOS_BEF_INDEX");
      if( tmpVar != null) {
         try {
             dscode.LOS_BEF_INDEX = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"LOS_BEF_INDEX\".");
         }
      }
      else 
         dscode.LOS_BEF_INDEX = Double.NaN;
 
      tmpVar = indata.get("MED_HH_INC_ZIP");
      if( tmpVar != null) {
         try {
             dscode.MED_HH_INC_ZIP = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MED_HH_INC_ZIP\".");
         }
      }
      else 
         dscode.MED_HH_INC_ZIP = Double.NaN;
 
      tmpVar = indata.get("ME_COMPLIANCE_COND17");
      if( tmpVar != null) {
         try {
             dscode.ME_COMPLIANCE_COND17 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ME_COMPLIANCE_COND17\".");
         }
      }
      else 
         dscode.ME_COMPLIANCE_COND17 = Double.NaN;
 
      tmpVar = indata.get("MK_48");
      if( tmpVar != null) {
         try {
             dscode.MK_48 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_48\".");
         }
      }
      else 
         dscode.MK_48 = Double.NaN;
 
      tmpVar = indata.get("MK_MDC_5");
      if( tmpVar != null) {
         try {
             dscode.MK_MDC_5 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_MDC_5\".");
         }
      }
      else 
         dscode.MK_MDC_5 = Double.NaN;
 
      tmpVar = indata.get("MK_MDC_6");
      if( tmpVar != null) {
         try {
             dscode.MK_MDC_6 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_MDC_6\".");
         }
      }
      else 
         dscode.MK_MDC_6 = Double.NaN;
 
      tmpVar = indata.get("PCT_ACUTE_ME");
      if( tmpVar != null) {
         try {
             dscode.PCT_ACUTE_ME = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PCT_ACUTE_ME\".");
         }
      }
      else 
         dscode.PCT_ACUTE_ME = Double.NaN;
 
      tmpVar = indata.get("PCT_COMPLIANT_ME");
      if( tmpVar != null) {
         try {
             dscode.PCT_COMPLIANT_ME = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PCT_COMPLIANT_ME\".");
         }
      }
      else 
         dscode.PCT_COMPLIANT_ME = Double.NaN;
 
      tmpVar = indata.get("PCT_COMP_PHARMACY_ME");
      if( tmpVar != null) {
         try {
             dscode.PCT_COMP_PHARMACY_ME = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PCT_COMP_PHARMACY_ME\".");
         }
      }
      else 
         dscode.PCT_COMP_PHARMACY_ME = Double.NaN;
 
      tmpVar = indata.get("PRIMARY_CARE_RATE_PRE12");
      if( tmpVar != null) {
         try {
             dscode.PRIMARY_CARE_RATE_PRE12 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PRIMARY_CARE_RATE_PRE12\".");
         }
      }
      else 
         dscode.PRIMARY_CARE_RATE_PRE12 = Double.NaN;
 
      tmpVar = indata.get("PRIMARY_CARE_RATE_PRE3");
      if( tmpVar != null) {
         try {
             dscode.PRIMARY_CARE_RATE_PRE3 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PRIMARY_CARE_RATE_PRE3\".");
         }
      }
      else 
         dscode.PRIMARY_CARE_RATE_PRE3 = Double.NaN;
 
      tmpVar = indata.get("PRIOR_60D_RE_ADMIT");
      if( tmpVar != null) {
         try {
             dscode.PRIOR_60D_RE_ADMIT = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PRIOR_60D_RE_ADMIT\".");
         }
      }
      else 
         dscode.PRIOR_60D_RE_ADMIT = Double.NaN;
 
      tmpVar = indata.get("PROC_GROUP_154");
      if( tmpVar != null) {
         try {
             dscode.PROC_GROUP_154 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PROC_GROUP_154\".");
         }
      }
      else 
         dscode.PROC_GROUP_154 = Double.NaN;
 
      tmpVar = indata.get("PROC_GROUP_160");
      if( tmpVar != null) {
         try {
             dscode.PROC_GROUP_160 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PROC_GROUP_160\".");
         }
      }
      else 
         dscode.PROC_GROUP_160 = Double.NaN;
 
      tmpVar = indata.get("PROC_RATE_PRE12");
      if( tmpVar != null) {
         try {
             dscode.PROC_RATE_PRE12 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PROC_RATE_PRE12\".");
         }
      }
      else 
         dscode.PROC_RATE_PRE12 = Double.NaN;
 
      tmpVar = indata.get("PROC_RATE_PRE3");
      if( tmpVar != null) {
         try {
             dscode.PROC_RATE_PRE3 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PROC_RATE_PRE3\".");
         }
      }
      else 
         dscode.PROC_RATE_PRE3 = Double.NaN;
 
      tmpVar = indata.get("REGIONS");
      if( tmpVar != null) {
         try {
            dscode.REGIONS = (String)tmpVar;
         } catch (Exception ex) {
            throw new JscoreException("Invalid input data type for String \"REGIONS\".");
         }
      }
      else 
         dscode.REGIONS = " ";
 
 
      dscode.run();
 
      outputVariables.put("I_RE_ADMIT_30D",((Object)dscode.I_RE_ADMIT_30D));
      outputVariables.put("OPT_CNT_ME",((Object)dscode.OPT_CNT_ME));
      outputVariables.put("OPT_CNT_MK",((Object)dscode.OPT_CNT_MK));
      outputVariables.put("OPT_DIAG_RATE_PRE12",((Object)dscode.OPT_DIAG_RATE_PRE12));
      outputVariables.put("OPT_DIAG_RATE_PRE3",((Object)dscode.OPT_DIAG_RATE_PRE3));
      outputVariables.put("OPT_ER_RATE_PRE12",((Object)dscode.OPT_ER_RATE_PRE12));
      outputVariables.put("OPT_ER_RATE_PRE3",((Object)dscode.OPT_ER_RATE_PRE3));
      outputVariables.put("OPT_HOSP_RATE_PRE12",((Object)dscode.OPT_HOSP_RATE_PRE12));
      outputVariables.put("OPT_HOSP_RATE_PRE3",((Object)dscode.OPT_HOSP_RATE_PRE3));
      outputVariables.put("OPT_ICU_RATE_PRE12",((Object)dscode.OPT_ICU_RATE_PRE12));
      outputVariables.put("OPT_ICU_RATE_PRE3",((Object)dscode.OPT_ICU_RATE_PRE3));
      outputVariables.put("OPT_LOS_BEF_INDEX",((Object)dscode.OPT_LOS_BEF_INDEX));
      outputVariables.put("OPT_MED_HH_INC_ZIP",((Object)dscode.OPT_MED_HH_INC_ZIP));
      outputVariables.put("OPT_PCT_ACUTE_ME",((Object)dscode.OPT_PCT_ACUTE_ME));
      outputVariables.put("OPT_PCT_COMPLIANT_ME",((Object)dscode.OPT_PCT_COMPLIANT_ME));
      outputVariables.put("OPT_PCT_COMP_PHARMACY_ME",((Object)dscode.OPT_PCT_COMP_PHARMACY_ME));
      outputVariables.put("OPT_PRIMARY_CARE_RATE_PRE12",((Object)dscode.OPT_PRIMARY_CARE_RATE_PRE12));
      outputVariables.put("OPT_PRIMARY_CARE_RATE_PRE3",((Object)dscode.OPT_PRIMARY_CARE_RATE_PRE3));
      outputVariables.put("OPT_PROC_RATE_PRE12",((Object)dscode.OPT_PROC_RATE_PRE12));
      outputVariables.put("OPT_PROC_RATE_PRE3",((Object)dscode.OPT_PROC_RATE_PRE3));
      outputVariables.put("P_RE_ADMIT_30D0",((Object)new Double(dscode.P_RE_ADMIT_30D0)));
      outputVariables.put("P_RE_ADMIT_30D1",((Object)new Double(dscode.P_RE_ADMIT_30D1)));
      outputVariables.put("U_RE_ADMIT_30D",((Object)new Double(dscode.U_RE_ADMIT_30D)));
      outputVariables.put("_WARN_",((Object)dscode._WARN_));
      return outputVariables;
   }
 
} // end class Score
