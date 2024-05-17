package com.xiaopeng.speech.protocol.bean.search;

import com.xiaopeng.speech.common.util.DontProguardClass;
import com.xiaopeng.speech.protocol.node.navi.bean.ChargeBean;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
@DontProguardClass
/* loaded from: classes23.dex */
public class ChargeData {
    private List<ChargingStationData> searchData;

    public List<ChargingStationData> getSearchData() {
        return this.searchData;
    }

    @DontProguardClass
    /* loaded from: classes23.dex */
    public static class ChargingStationData {
        public static final int CHARGING_SERVICE_SELF = 1;
        private int acFreeNums;
        private int acNums;
        private boolean busiState;
        private String busiTime;
        private float chargingFee;
        private List<ChargeBean> content;
        private int dcFreeNums;
        private int dcNums;
        private String displayDistance;
        private String displayExpRemainDis;
        private float distance;
        private float expRemainDis;
        private FreeNumPre freeNumPre;
        private String locationDes;
        private String name;
        private String operName;
        private String payment;
        private int selfSupportFlag;
        private float serviceFee;
        private String stationAddr;
        private String stationId;

        public String getStationId() {
            return this.stationId;
        }

        public String getName() {
            return this.name;
        }

        public String getDisplayDistance() {
            return this.displayDistance;
        }

        public void setDisplayDistance(String displayDistance) {
            this.displayDistance = displayDistance;
        }

        public String getDisplayExpRemainDis() {
            return this.displayExpRemainDis;
        }

        public void setDisplayExpRemainDis(String displayExpRemainDis) {
            this.displayExpRemainDis = displayExpRemainDis;
        }

        public int getSelfSupportFlag() {
            return this.selfSupportFlag;
        }

        public String getStationAddr() {
            return this.stationAddr;
        }

        public float getExpRemainDis() {
            return this.expRemainDis;
        }

        public float getDistance() {
            return this.distance;
        }

        public int getDcNums() {
            return this.dcNums;
        }

        public int getDcFreeNums() {
            return this.dcFreeNums;
        }

        public int getAcNums() {
            return this.acNums;
        }

        public int getAcFreeNums() {
            return this.acFreeNums;
        }

        public float getChargingFee() {
            return this.chargingFee;
        }

        public float getServiceFee() {
            return this.serviceFee;
        }

        public void setBusiTime(String busiTime) {
            this.busiTime = busiTime;
        }

        public String getBusiTime() {
            return this.busiTime;
        }

        public String getLocationDes() {
            return this.locationDes;
        }

        public FreeNumPre getFreeNumPre() {
            return this.freeNumPre;
        }

        public String getOperName() {
            return this.operName;
        }

        public void setStationId(String stationId) {
            this.stationId = stationId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSelfSupportFlag(int selfSupportFlag) {
            this.selfSupportFlag = selfSupportFlag;
        }

        public void setStationAddr(String stationAddr) {
            this.stationAddr = stationAddr;
        }

        public void setExpRemainDis(float expRemainDis) {
            this.expRemainDis = expRemainDis;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        public void setDcNums(int dcNums) {
            this.dcNums = dcNums;
        }

        public void setDcFreeNums(int dcFreeNums) {
            this.dcFreeNums = dcFreeNums;
        }

        public void setAcNums(int acNums) {
            this.acNums = acNums;
        }

        public void setAcFreeNums(int acFreeNums) {
            this.acFreeNums = acFreeNums;
        }

        public void setChargingFee(float chargingFee) {
            this.chargingFee = chargingFee;
        }

        public void setServiceFee(float serviceFee) {
            this.serviceFee = serviceFee;
        }

        public void setLocationDes(String locationDes) {
            this.locationDes = locationDes;
        }

        public void setOperName(String operName) {
            this.operName = operName;
        }

        public boolean getBusiState() {
            return this.busiState;
        }

        public void setBusiState(boolean busiState) {
            this.busiState = busiState;
        }

        public String getPayment() {
            return this.payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public List<ChargeBean> getContent() {
            return this.content;
        }

        public void setContent(List<ChargeBean> content) {
            this.content = content;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("stationId", getStationId());
            json.put("name", getName());
            json.put("selfSupportFlag", this.selfSupportFlag);
            json.put("stationAddr", this.stationAddr);
            json.put("expRemainDis", this.expRemainDis);
            json.put("distance", this.distance);
            json.put("dcNums", this.dcNums);
            json.put("dcFreeNums", this.dcFreeNums);
            json.put("acNums", this.acNums);
            json.put("acFreeNums", this.acFreeNums);
            json.put("chargingFee", this.chargingFee);
            json.put("serviceFee", this.serviceFee);
            json.put("busiTime", this.busiTime);
            json.put("locationDes", this.locationDes);
            json.put("operName", this.operName);
            json.put("displayDistance", this.displayDistance);
            json.put("displayExpRemainDis", this.displayExpRemainDis);
            json.put("busiState", this.busiState);
            json.put("payment", this.payment);
            JSONArray jsArray = new JSONArray();
            List<ChargeBean> list = this.content;
            if (list != null && list.size() > 0) {
                for (ChargeBean chargeBean : this.content) {
                    jsArray.put(chargeBean.toJson());
                }
            }
            json.put("content", jsArray);
            return json;
        }

        public String toString() {
            return "ChargingStationData{stationId='" + this.stationId + "', name='" + this.name + "', selfSupportFlag=" + this.selfSupportFlag + ", stationAddr='" + this.stationAddr + "', expRemainDis=" + this.expRemainDis + ", distance=" + this.distance + ", dcNums=" + this.dcNums + ", dcFreeNums=" + this.dcFreeNums + ", acNums=" + this.acNums + ", acFreeNums=" + this.acFreeNums + ", chargingFee=" + this.chargingFee + ", serviceFee=" + this.serviceFee + ", busiTime='" + this.busiTime + "', locationDes='" + this.locationDes + "', operName=" + this.operName + ", displayDistance=" + this.displayDistance + ", displayExpRemainDis=" + this.displayExpRemainDis + ", content='" + this.content + ", freeNumPre=" + this.freeNumPre + '}';
        }
    }

    @DontProguardClass
    /* loaded from: classes23.dex */
    public static class FreeNumPre {
        private List<Integer> acNums;
        private List<Integer> dcNums;
        private int interval;
        private boolean showHistory;
        private List<Integer> timePoint;
        private long timeStamp;

        public int getInterval() {
            return this.interval;
        }

        public List<Integer> getTimePoints() {
            return this.timePoint;
        }

        public List<Integer> getDcNums() {
            return this.dcNums;
        }

        public List<Integer> getAcNums() {
            return this.acNums;
        }

        public boolean isShowHistory() {
            return this.showHistory;
        }

        public long getTimeStamp() {
            return this.timeStamp;
        }
    }

    public String toString() {
        return "ChargeData{searchData=" + this.searchData + '}';
    }
}
