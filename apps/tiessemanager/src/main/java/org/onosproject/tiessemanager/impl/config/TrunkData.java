package org.onosproject.tiessemanager.impl.config;

import java.util.List;

public class TrunkData {

    private String port;
    private List<TrunkParams> trunkParamsList;

    public TrunkData(String port, List<TrunkParams> trunkParamsList) {
        this.port = port;
        this.trunkParamsList = trunkParamsList;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<TrunkParams> getTrunkParamsList() {
        return trunkParamsList;
    }

    public void setTrunkParamsList(List<TrunkParams> trunkParamsList) {
        this.trunkParamsList = trunkParamsList;
    }
}
