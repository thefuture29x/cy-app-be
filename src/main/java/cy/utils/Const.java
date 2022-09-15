package cy.utils;

public class Const {
    public enum tableName{
        PROJECT,
        FEATURE,
        TASK,
        SUBTASK,
        BUG,
        BUG_HISTORY,
        FILE,
        TAG,
        HISTORY,
        COMMENT,
        USER_PROJECT,
        TAG_RELATION,
    }

    public enum type{
        TYPE_DEV,
        TYPE_FOLLOWER,
        TYPE_VIEWER,
    }

    public enum status{
        TO_DO,
        IN_PROGRESS,
        PENDING,
        IN_REVIEW,
        DONE,
        FIX_BUG,
    }
    public enum priority{
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW,
    }

}
