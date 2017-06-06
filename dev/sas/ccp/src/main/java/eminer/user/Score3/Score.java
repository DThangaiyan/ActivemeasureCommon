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
 
package eminer.user.Score3;
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
 
      tmpVar = indata.get("BRAIN_CANCER_E");
      if( tmpVar != null) {
         try {
             dscode.BRAIN_CANCER_E = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"BRAIN_CANCER_E\".");
         }
      }
      else 
         dscode.BRAIN_CANCER_E = Double.NaN;
 
      tmpVar = indata.get("CHEMO_E");
      if( tmpVar != null) {
         try {
             dscode.CHEMO_E = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"CHEMO_E\".");
         }
      }
      else 
         dscode.CHEMO_E = Double.NaN;
 
      tmpVar = indata.get("CHRONIC_CNT");
      if( tmpVar != null) {
         try {
             dscode.CHRONIC_CNT = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"CHRONIC_CNT\".");
         }
      }
      else 
         dscode.CHRONIC_CNT = Double.NaN;
 
      tmpVar = indata.get("CKD_STAGE4");
      if( tmpVar != null) {
         try {
             dscode.CKD_STAGE4 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"CKD_STAGE4\".");
         }
      }
      else 
         dscode.CKD_STAGE4 = Double.NaN;
 
      tmpVar = indata.get("DEMENTIA");
      if( tmpVar != null) {
         try {
             dscode.DEMENTIA = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"DEMENTIA\".");
         }
      }
      else 
         dscode.DEMENTIA = Double.NaN;
 
      tmpVar = indata.get("DIAGCLM_DAYS_PRE09");
      if( tmpVar != null) {
         try {
             dscode.DIAGCLM_DAYS_PRE09 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"DIAGCLM_DAYS_PRE09\".");
         }
      }
      else 
         dscode.DIAGCLM_DAYS_PRE09 = Double.NaN;
 
      tmpVar = indata.get("HOSPNONICU_DAYS_PRE06");
      if( tmpVar != null) {
         try {
             dscode.HOSPNONICU_DAYS_PRE06 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"HOSPNONICU_DAYS_PRE06\".");
         }
      }
      else 
         dscode.HOSPNONICU_DAYS_PRE06 = Double.NaN;
 
      tmpVar = indata.get("HOSP_DAYS_PRE06");
      if( tmpVar != null) {
         try {
             dscode.HOSP_DAYS_PRE06 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"HOSP_DAYS_PRE06\".");
         }
      }
      else 
         dscode.HOSP_DAYS_PRE06 = Double.NaN;
 
      tmpVar = indata.get("ICU_DAYS_PRE06");
      if( tmpVar != null) {
         try {
             dscode.ICU_DAYS_PRE06 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ICU_DAYS_PRE06\".");
         }
      }
      else 
         dscode.ICU_DAYS_PRE06 = Double.NaN;
 
      tmpVar = indata.get("I_DEATH");
      if( tmpVar != null) {
         try {
            dscode.I_DEATH = (String)tmpVar;
         } catch (Exception ex) {
            throw new JscoreException("Invalid input data type for String \"I_DEATH\".");
         }
      }
      else 
         dscode.I_DEATH = " ";
 
      tmpVar = indata.get("MEDCLM_DAYS_PRE02");
      if( tmpVar != null) {
         try {
             dscode.MEDCLM_DAYS_PRE02 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MEDCLM_DAYS_PRE02\".");
         }
      }
      else 
         dscode.MEDCLM_DAYS_PRE02 = Double.NaN;
 
      tmpVar = indata.get("METAS_CANCER_E");
      if( tmpVar != null) {
         try {
             dscode.METAS_CANCER_E = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"METAS_CANCER_E\".");
         }
      }
      else 
         dscode.METAS_CANCER_E = Double.NaN;
 
      tmpVar = indata.get("ME_2043");
      if( tmpVar != null) {
         try {
             dscode.ME_2043 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ME_2043\".");
         }
      }
      else 
         dscode.ME_2043 = Double.NaN;
 
      tmpVar = indata.get("ME_62");
      if( tmpVar != null) {
         try {
             dscode.ME_62 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ME_62\".");
         }
      }
      else 
         dscode.ME_62 = Double.NaN;
 
      tmpVar = indata.get("ME_708");
      if( tmpVar != null) {
         try {
             dscode.ME_708 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ME_708\".");
         }
      }
      else 
         dscode.ME_708 = Double.NaN;
 
      tmpVar = indata.get("ME_75");
      if( tmpVar != null) {
         try {
             dscode.ME_75 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"ME_75\".");
         }
      }
      else 
         dscode.ME_75 = Double.NaN;
 
      tmpVar = indata.get("MK_1");
      if( tmpVar != null) {
         try {
             dscode.MK_1 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_1\".");
         }
      }
      else 
         dscode.MK_1 = Double.NaN;
 
      tmpVar = indata.get("MK_1019");
      if( tmpVar != null) {
         try {
             dscode.MK_1019 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_1019\".");
         }
      }
      else 
         dscode.MK_1019 = Double.NaN;
 
      tmpVar = indata.get("MK_175");
      if( tmpVar != null) {
         try {
             dscode.MK_175 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_175\".");
         }
      }
      else 
         dscode.MK_175 = Double.NaN;
 
      tmpVar = indata.get("MK_186");
      if( tmpVar != null) {
         try {
             dscode.MK_186 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_186\".");
         }
      }
      else 
         dscode.MK_186 = Double.NaN;
 
      tmpVar = indata.get("MK_23");
      if( tmpVar != null) {
         try {
             dscode.MK_23 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_23\".");
         }
      }
      else 
         dscode.MK_23 = Double.NaN;
 
      tmpVar = indata.get("MK_38");
      if( tmpVar != null) {
         try {
             dscode.MK_38 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_38\".");
         }
      }
      else 
         dscode.MK_38 = Double.NaN;
 
      tmpVar = indata.get("MK_5");
      if( tmpVar != null) {
         try {
             dscode.MK_5 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_5\".");
         }
      }
      else 
         dscode.MK_5 = Double.NaN;
 
      tmpVar = indata.get("MK_51");
      if( tmpVar != null) {
         try {
             dscode.MK_51 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_51\".");
         }
      }
      else 
         dscode.MK_51 = Double.NaN;
 
      tmpVar = indata.get("MK_64");
      if( tmpVar != null) {
         try {
             dscode.MK_64 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_64\".");
         }
      }
      else 
         dscode.MK_64 = Double.NaN;
 
      tmpVar = indata.get("MK_7");
      if( tmpVar != null) {
         try {
             dscode.MK_7 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_7\".");
         }
      }
      else 
         dscode.MK_7 = Double.NaN;
 
      tmpVar = indata.get("MK_81");
      if( tmpVar != null) {
         try {
             dscode.MK_81 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_81\".");
         }
      }
      else 
         dscode.MK_81 = Double.NaN;
 
      tmpVar = indata.get("MK_86");
      if( tmpVar != null) {
         try {
             dscode.MK_86 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_86\".");
         }
      }
      else 
         dscode.MK_86 = Double.NaN;
 
      tmpVar = indata.get("MK_9");
      if( tmpVar != null) {
         try {
             dscode.MK_9 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"MK_9\".");
         }
      }
      else 
         dscode.MK_9 = Double.NaN;
 
      tmpVar = indata.get("PATSEX");
      if( tmpVar != null) {
         try {
             dscode.PATSEX = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PATSEX\".");
         }
      }
      else 
         dscode.PATSEX = Double.NaN;
 
      tmpVar = indata.get("PTAGE");
      if( tmpVar != null) {
         try {
             dscode.PTAGE = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"PTAGE\".");
         }
      }
      else 
         dscode.PTAGE = Double.NaN;
 
      tmpVar = indata.get("P_DEATH0");
      if( tmpVar != null) {
         try {
             dscode.P_DEATH0 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"P_DEATH0\".");
         }
      }
      else 
         dscode.P_DEATH0 = Double.NaN;
 
      tmpVar = indata.get("P_DEATH1");
      if( tmpVar != null) {
         try {
             dscode.P_DEATH1 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"P_DEATH1\".");
         }
      }
      else 
         dscode.P_DEATH1 = Double.NaN;
 
      tmpVar = indata.get("Q_DEATH0");
      if( tmpVar != null) {
         try {
             dscode.Q_DEATH0 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"Q_DEATH0\".");
         }
      }
      else 
         dscode.Q_DEATH0 = Double.NaN;
 
      tmpVar = indata.get("Q_DEATH1");
      if( tmpVar != null) {
         try {
             dscode.Q_DEATH1 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"Q_DEATH1\".");
         }
      }
      else 
         dscode.Q_DEATH1 = Double.NaN;
 
      tmpVar = indata.get("RESP_FAIL_FLAG");
      if( tmpVar != null) {
         try {
             dscode.RESP_FAIL_FLAG = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"RESP_FAIL_FLAG\".");
         }
      }
      else 
         dscode.RESP_FAIL_FLAG = Double.NaN;
 
      tmpVar = indata.get("RXNDC_PRE03");
      if( tmpVar != null) {
         try {
             dscode.RXNDC_PRE03 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"RXNDC_PRE03\".");
         }
      }
      else 
         dscode.RXNDC_PRE03 = Double.NaN;
 
      tmpVar = indata.get("U_DEATH");
      if( tmpVar != null) {
         try {
             dscode.U_DEATH = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"U_DEATH\".");
         }
      }
      else 
         dscode.U_DEATH = Double.NaN;
 
      tmpVar = indata.get("V_DEATH0");
      if( tmpVar != null) {
         try {
             dscode.V_DEATH0 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"V_DEATH0\".");
         }
      }
      else 
         dscode.V_DEATH0 = Double.NaN;
 
      tmpVar = indata.get("V_DEATH1");
      if( tmpVar != null) {
         try {
             dscode.V_DEATH1 = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"V_DEATH1\".");
         }
      }
      else 
         dscode.V_DEATH1 = Double.NaN;
 
      tmpVar = indata.get("WEIGHT_LOSS_E");
      if( tmpVar != null) {
         try {
             dscode.WEIGHT_LOSS_E = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"WEIGHT_LOSS_E\".");
         }
      }
      else 
         dscode.WEIGHT_LOSS_E = Double.NaN;
 
      tmpVar = indata.get("_NODE_");
      if( tmpVar != null) {
         try {
             dscode._NODE_ = ((Double)tmpVar).doubleValue();
         } catch (Exception ex) {
             throw new JscoreException("Invalid input data type for Double \"_NODE_\".");
         }
      }
      else 
         dscode._NODE_ = Double.NaN;
 
 
      dscode.run();
 
      outputVariables.put("EM_CLASSIFICATION",((Object)dscode.EM_CLASSIFICATION));
      outputVariables.put("EM_EVENTPROBABILITY",((Object)new Double(dscode.EM_EVENTPROBABILITY)));
      outputVariables.put("EM_PROBABILITY",((Object)new Double(dscode.EM_PROBABILITY)));
      outputVariables.put("EM_SEGMENT",((Object)new Double(dscode.EM_SEGMENT)));
      outputVariables.put("OPT_CHRONIC_CNT",((Object)dscode.OPT_CHRONIC_CNT));
      outputVariables.put("OPT_DIAGCLM_DAYS_PRE09",((Object)dscode.OPT_DIAGCLM_DAYS_PRE09));
      outputVariables.put("OPT_HOSPNONICU_DAYS_PRE06",((Object)dscode.OPT_HOSPNONICU_DAYS_PRE06));
      outputVariables.put("OPT_ICU_DAYS_PRE06",((Object)dscode.OPT_ICU_DAYS_PRE06));
      outputVariables.put("OPT_MEDCLM_DAYS_PRE02",((Object)dscode.OPT_MEDCLM_DAYS_PRE02));
      outputVariables.put("OPT_RXNDC_PRE03",((Object)dscode.OPT_RXNDC_PRE03));
      outputVariables.put("_WARN_",((Object)dscode._WARN_));
      return outputVariables;
   }
 
} // end class Score

