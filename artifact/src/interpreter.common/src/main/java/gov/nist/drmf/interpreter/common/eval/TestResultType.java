package gov.nist.drmf.interpreter.common.eval;

public enum TestResultType {
    SUCCESS, FAILURE, ERROR, SKIPPED;

    public TestResultType and(TestResultType type) {
        TestResultType result = SUCCESS;
        if ( SKIPPED.equals(this) ) {
            return type;
        } else if ( SKIPPED.equals(type) ) {
            return this;
        } else if ( SUCCESS.equals(this) ) {
            if ( SUCCESS.equals(type) ) result = this;
            else if ( FAILURE.equals(type) ) result = FAILURE;
            else result = ERROR;
        } else if ( FAILURE.equals(this) ) {
            if ( ERROR.equals(type) ) result = ERROR;
            else result = FAILURE;
        } else result = ERROR;
        return result;
    }
}
