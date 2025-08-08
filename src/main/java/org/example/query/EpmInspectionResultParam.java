package org.example.query;

import java.util.List;

/**
 * 巡检结果查询参数类
 * 用于适配原有的EpmInspectionResultParam
 */
public class EpmInspectionResultParam {
    
    private Criteria criteria;

    public Criteria createCriteria() {
        if (criteria == null) {
            criteria = new Criteria();
        }
        return criteria;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    /**
     * 查询条件内部类
     */
    public static class Criteria {
        private String env;
        private String isDeleted;
        private String inspectionType;
        private String bunitTag;
        private List<Integer> idList;

        public Criteria andEnvEqualTo(String env) {
            this.env = env;
            return this;
        }

        public Criteria andIsDeletedEqualTo(String isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public Criteria andInspectionTypeEqualTo(String inspectionType) {
            this.inspectionType = inspectionType;
            return this;
        }

        public Criteria andBunitTagEqualTo(String bunitTag) {
            this.bunitTag = bunitTag;
            return this;
        }

        public Criteria andIdIn(List<Integer> idList) {
            this.idList = idList;
            return this;
        }

        // Getters
        public String getEnv() {
            return env;
        }

        public String getIsDeleted() {
            return isDeleted;
        }

        public String getInspectionType() {
            return inspectionType;
        }

        public String getBunitTag() {
            return bunitTag;
        }

        public List<Integer> getIdList() {
            return idList;
        }
    }
}