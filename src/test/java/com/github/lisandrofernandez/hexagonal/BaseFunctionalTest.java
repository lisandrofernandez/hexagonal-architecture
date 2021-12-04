package com.github.lisandrofernandez.hexagonal;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/db/clear-and-init-config-data.sql")
@SqlMergeMode(MergeMode.MERGE)
public abstract class BaseFunctionalTest {
    protected static final String TOPIC_NAME_CMD_USER_ACCOUNT = "cmd.user-account";
    protected static final String TOPIC_NAME_FTC_USER_ACCOUNT = "fct.user-account";
}
