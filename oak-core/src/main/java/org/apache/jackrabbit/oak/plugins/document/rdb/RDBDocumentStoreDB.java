begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|rdb
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBJDBCTools
operator|.
name|closeResultSet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBJDBCTools
operator|.
name|closeStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBJDBCTools
operator|.
name|PreparedStatementComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Defines variation in the capabilities of different RDBs.  */
end_comment

begin_enum
specifier|public
enum|enum
name|RDBDocumentStoreDB
block|{
name|DEFAULT
argument_list|(
literal|"default"
argument_list|)
block|{     }
block|,
name|H2
argument_list|(
literal|"H2"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInitializationStatement
parameter_list|()
block|{
return|return
literal|"create alias if not exists unix_timestamp as $$ long unix_timestamp() { return System.currentTimeMillis()/1000L; } $$;"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCurrentTimeStampInSecondsSyntax
parameter_list|()
block|{
return|return
literal|"select unix_timestamp()"
return|;
block|}
block|}
block|,
name|DERBY
argument_list|(
literal|"Apache Derby"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowsCaseInSelect
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|,
name|POSTGRES
argument_list|(
literal|"PostgreSQL"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
name|String
name|result
init|=
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|9
argument_list|,
literal|5
argument_list|,
literal|9
argument_list|,
literal|4
argument_list|,
name|description
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// special case: we need 9.4.1208 or newer (see OAK-3977)
if|if
condition|(
name|md
operator|.
name|getDriverMajorVersion
argument_list|()
operator|==
literal|9
operator|&&
name|md
operator|.
name|getDriverMinorVersion
argument_list|()
operator|==
literal|4
condition|)
block|{
name|String
name|versionString
init|=
name|md
operator|.
name|getDriverVersion
argument_list|()
decl_stmt|;
name|String
name|scanfor
init|=
literal|"9.4."
decl_stmt|;
name|int
name|p
init|=
name|versionString
operator|.
name|indexOf
argument_list|(
name|scanfor
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>=
literal|0
condition|)
block|{
name|StringBuilder
name|build
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|versionString
operator|.
name|substring
argument_list|(
name|p
operator|+
name|scanfor
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|toCharArray
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|>=
literal|'0'
operator|&&
name|c
operator|<=
literal|'9'
condition|)
block|{
name|build
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|build
operator|.
name|toString
argument_list|()
argument_list|)
operator|<
literal|1208
condition|)
block|{
name|result
operator|=
literal|"Unsupported "
operator|+
name|description
operator|+
literal|" driver version: "
operator|+
name|md
operator|.
name|getDriverVersion
argument_list|()
operator|+
literal|", found build "
operator|+
name|build
operator|+
literal|", but expected at least build 1208"
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCurrentTimeStampInSecondsSyntax
parameter_list|()
block|{
return|return
literal|"select extract(epoch from now())::integer"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
operator|(
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, DATA varchar(16384), BDATA bytea)"
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAdditionalDiagnostics
parameter_list|(
name|RDBConnectionHandler
name|ch
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|Connection
name|con
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|stmt
init|=
literal|null
decl_stmt|;
name|ResultSet
name|rs
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|con
operator|=
name|ch
operator|.
name|getROConnection
argument_list|()
expr_stmt|;
name|String
name|cat
init|=
name|con
operator|.
name|getCatalog
argument_list|()
decl_stmt|;
name|stmt
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"SELECT pg_encoding_to_char(encoding), datcollate FROM pg_database WHERE datname=?"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|cat
argument_list|)
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
literal|"pg_encoding_to_char(encoding)"
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"datcollate"
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"while getting diagnostics"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeResultSet
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|closeStatement
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|,
name|DB2
argument_list|(
literal|"DB2"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCurrentTimeStampInSecondsSyntax
parameter_list|()
block|{
return|return
literal|"select cast (days(current_timestamp - current_timezone) - days('1970-01-01') as integer) * 86400 + midnight_seconds(current_timestamp - current_timezone) from sysibm.sysdummy1"
return|;
block|}
specifier|public
name|String
name|getTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar(512) not null, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, DATA varchar(16384), BDATA blob("
operator|+
literal|1024
operator|*
literal|1024
operator|*
literal|1024
operator|+
literal|"))"
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getIndexCreationStatements
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|statements
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|pkName
init|=
name|tableName
operator|+
literal|"_pk"
decl_stmt|;
name|statements
operator|.
name|add
argument_list|(
literal|"create unique index "
operator|+
name|pkName
operator|+
literal|" on "
operator|+
name|tableName
operator|+
literal|" ( ID ) cluster"
argument_list|)
expr_stmt|;
name|statements
operator|.
name|add
argument_list|(
literal|"alter table "
operator|+
name|tableName
operator|+
literal|" add constraint "
operator|+
name|pkName
operator|+
literal|" primary key ( ID )"
argument_list|)
expr_stmt|;
name|statements
operator|.
name|addAll
argument_list|(
name|super
operator|.
name|getIndexCreationStatements
argument_list|(
name|tableName
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|statements
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAdditionalDiagnostics
parameter_list|(
name|RDBConnectionHandler
name|ch
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|Connection
name|con
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|stmt
init|=
literal|null
decl_stmt|;
name|ResultSet
name|rs
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|con
operator|=
name|ch
operator|.
name|getROConnection
argument_list|()
expr_stmt|;
comment|// schema name will only be available with JDK 1.7
name|String
name|conSchema
init|=
name|ch
operator|.
name|getSchema
argument_list|(
name|con
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"SELECT CODEPAGE, COLLATIONSCHEMA, COLLATIONNAME, TABSCHEMA FROM SYSCAT.COLUMNS WHERE COLNAME=? and COLNO=0 AND UPPER(TABNAME)=UPPER(?)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|conSchema
operator|!=
literal|null
condition|)
block|{
name|conSchema
operator|=
name|conSchema
operator|.
name|trim
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" AND UPPER(TABSCHEMA)=UPPER(?)"
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
literal|"ID"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
if|if
condition|(
name|conSchema
operator|!=
literal|null
condition|)
block|{
name|stmt
operator|.
name|setString
argument_list|(
literal|3
argument_list|,
name|conSchema
argument_list|)
expr_stmt|;
block|}
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
operator|&&
name|result
operator|.
name|size
argument_list|()
operator|<
literal|20
condition|)
block|{
name|String
name|schema
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"TABSCHEMA"
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|schema
operator|+
literal|".CODEPAGE"
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|"CODEPAGE"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
name|schema
operator|+
literal|".COLLATIONSCHEMA"
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|"COLLATIONSCHEMA"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
name|schema
operator|+
literal|".COLLATIONNAME"
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|"COLLATIONNAME"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"while getting diagnostics"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeResultSet
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|closeStatement
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|,
name|ORACLE
argument_list|(
literal|"Oracle"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCurrentTimeStampInSecondsSyntax
parameter_list|()
block|{
return|return
literal|"select (trunc(sys_extract_utc(systimestamp)) - to_date('01/01/1970', 'MM/DD/YYYY')) * 24 * 60 * 60 + to_number(to_char(sys_extract_utc(systimestamp), 'SSSSS')) from dual"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInitializationStatement
parameter_list|()
block|{
comment|// see https://issues.apache.org/jira/browse/OAK-1914
comment|// for some reason, the default for NLS_SORT is incorrect
return|return
operator|(
literal|"ALTER SESSION SET NLS_SORT='BINARY'"
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
comment|// see https://issues.apache.org/jira/browse/OAK-1914
return|return
operator|(
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar(512) not null primary key, MODIFIED number, HASBINARY number, DELETEDONCE number, MODCOUNT number, CMODCOUNT number, DSIZE number, DATA varchar(4000), BDATA blob)"
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAdditionalDiagnostics
parameter_list|(
name|RDBConnectionHandler
name|ch
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|Connection
name|con
init|=
literal|null
decl_stmt|;
name|Statement
name|stmt
init|=
literal|null
decl_stmt|;
name|ResultSet
name|rs
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|con
operator|=
name|ch
operator|.
name|getROConnection
argument_list|()
expr_stmt|;
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"SELECT PARAMETER, VALUE from NLS_DATABASE_PARAMETERS WHERE PARAMETER IN ('NLS_COMP', 'NLS_CHARACTERSET')"
argument_list|)
expr_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"while getting diagnostics"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeResultSet
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|closeStatement
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|,
name|MYSQL
argument_list|(
literal|"MySQL"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCurrentTimeStampInSecondsSyntax
parameter_list|()
block|{
return|return
literal|"select unix_timestamp()"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
comment|// see https://issues.apache.org/jira/browse/OAK-1913
return|return
operator|(
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varbinary(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, DATA varchar(16000), BDATA longblob)"
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FETCHFIRSTSYNTAX
name|getFetchFirstSyntax
parameter_list|()
block|{
return|return
name|FETCHFIRSTSYNTAX
operator|.
name|LIMIT
return|;
block|}
annotation|@
name|Override
specifier|public
name|PreparedStatementComponent
name|getConcatQuery
parameter_list|(
specifier|final
name|String
name|appendData
parameter_list|,
specifier|final
name|int
name|dataOctetLimit
parameter_list|)
block|{
return|return
operator|new
name|PreparedStatementComponent
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getStatementComponent
parameter_list|()
block|{
return|return
literal|"CONCAT(DATA, ?)"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|setParameters
parameter_list|(
name|PreparedStatement
name|stmt
parameter_list|,
name|int
name|startIndex
parameter_list|)
throws|throws
name|SQLException
block|{
name|stmt
operator|.
name|setString
argument_list|(
name|startIndex
operator|++
argument_list|,
name|appendData
argument_list|)
expr_stmt|;
return|return
name|startIndex
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAdditionalDiagnostics
parameter_list|(
name|RDBConnectionHandler
name|ch
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|Connection
name|con
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|stmt
init|=
literal|null
decl_stmt|;
name|ResultSet
name|rs
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|con
operator|=
name|ch
operator|.
name|getROConnection
argument_list|()
expr_stmt|;
name|stmt
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"SHOW TABLE STATUS LIKE ?"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
literal|"collation"
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|"Collation"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|stmt
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"SHOW VARIABLES WHERE variable_name LIKE 'character\\_set\\_%' OR variable_name LIKE 'collation%' OR variable_name = 'max_allowed_packet'"
argument_list|)
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"while getting diagnostics"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeResultSet
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|closeStatement
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|,
name|MSSQL
argument_list|(
literal|"Microsoft SQL Server"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|11
argument_list|,
literal|0
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
comment|// see https://issues.apache.org/jira/browse/OAK-2395
return|return
operator|(
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varbinary(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, DATA nvarchar(4000), BDATA varbinary(max))"
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FETCHFIRSTSYNTAX
name|getFetchFirstSyntax
parameter_list|()
block|{
return|return
name|FETCHFIRSTSYNTAX
operator|.
name|TOP
return|;
block|}
annotation|@
name|Override
specifier|public
name|PreparedStatementComponent
name|getConcatQuery
parameter_list|(
specifier|final
name|String
name|appendData
parameter_list|,
specifier|final
name|int
name|dataOctetLimit
parameter_list|)
block|{
return|return
operator|new
name|PreparedStatementComponent
argument_list|()
block|{
annotation|@
name|Override
comment|// this statement ensures that SQL server will generate an exception on overflow
specifier|public
name|String
name|getStatementComponent
parameter_list|()
block|{
return|return
literal|"CASE WHEN LEN(DATA)< ? THEN (DATA + CAST(? AS nvarchar("
operator|+
name|dataOctetLimit
operator|+
literal|"))) ELSE (DATA + CAST(DATA AS nvarchar(max))) END"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|setParameters
parameter_list|(
name|PreparedStatement
name|stmt
parameter_list|,
name|int
name|startIndex
parameter_list|)
throws|throws
name|SQLException
block|{
name|stmt
operator|.
name|setInt
argument_list|(
name|startIndex
operator|++
argument_list|,
name|dataOctetLimit
operator|-
name|appendData
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
name|startIndex
operator|++
argument_list|,
name|appendData
argument_list|)
expr_stmt|;
return|return
name|startIndex
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCurrentTimeStampInSecondsSyntax
parameter_list|()
block|{
return|return
literal|"select datediff(second, dateadd(second, datediff(second, getutcdate(), getdate()), '1970-01-01'), getdate())"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAdditionalDiagnostics
parameter_list|(
name|RDBConnectionHandler
name|ch
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|Connection
name|con
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|stmt
init|=
literal|null
decl_stmt|;
name|ResultSet
name|rs
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|con
operator|=
name|ch
operator|.
name|getROConnection
argument_list|()
expr_stmt|;
name|String
name|cat
init|=
name|con
operator|.
name|getCatalog
argument_list|()
decl_stmt|;
name|stmt
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"SELECT collation_name FROM sys.databases WHERE name=?"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|cat
argument_list|)
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
literal|"collation_name"
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"while getting diagnostics"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeResultSet
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|closeStatement
argument_list|(
name|stmt
argument_list|)
expr_stmt|;
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RDBDocumentStoreDB
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// whether to create indices
specifier|private
specifier|static
specifier|final
name|String
name|CREATEINDEX
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentStore.CREATEINDEX"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|public
enum|enum
name|FETCHFIRSTSYNTAX
block|{
name|FETCHFIRST
block|,
name|LIMIT
block|,
name|TOP
block|}
block|;
comment|/**      * Check the database brand and version      */
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|"Unknown database type: "
operator|+
name|md
operator|.
name|getDatabaseProductName
argument_list|()
return|;
block|}
comment|/**      * Allows case in select. Default true.      */
specifier|public
name|boolean
name|allowsCaseInSelect
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Query syntax for "FETCH FIRST"      */
specifier|public
name|FETCHFIRSTSYNTAX
name|getFetchFirstSyntax
parameter_list|()
block|{
return|return
name|FETCHFIRSTSYNTAX
operator|.
name|FETCHFIRST
return|;
block|}
comment|/**      * Query syntax for current time in ms since the epoch      *       * @return the query syntax or empty string when no such syntax is available      */
specifier|public
name|String
name|getCurrentTimeStampInSecondsSyntax
parameter_list|()
block|{
comment|// unfortunately, we don't have a portable statement for this
return|return
literal|""
return|;
block|}
comment|/**      * Returns the CONCAT function or its equivalent function or sub-query. Note      * that the function MUST NOT cause a truncated value to be written!      *      * @param appendData      *            string to be inserted      * @param dataOctetLimit      *            expected capacity of data column      */
specifier|public
name|PreparedStatementComponent
name|getConcatQuery
parameter_list|(
specifier|final
name|String
name|appendData
parameter_list|,
specifier|final
name|int
name|dataOctetLimit
parameter_list|)
block|{
return|return
operator|new
name|PreparedStatementComponent
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getStatementComponent
parameter_list|()
block|{
return|return
literal|"DATA || CAST(? AS varchar("
operator|+
name|dataOctetLimit
operator|+
literal|"))"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|setParameters
parameter_list|(
name|PreparedStatement
name|stmt
parameter_list|,
name|int
name|startIndex
parameter_list|)
throws|throws
name|SQLException
block|{
name|stmt
operator|.
name|setString
argument_list|(
name|startIndex
operator|++
argument_list|,
name|appendData
argument_list|)
expr_stmt|;
return|return
name|startIndex
return|;
block|}
block|}
return|;
block|}
comment|/**      * Query for any required initialization of the DB.      *       * @return the DB initialization SQL string      */
specifier|public
annotation|@
name|Nonnull
name|String
name|getInitializationStatement
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/**      * Table creation statement string      *      * @param tableName      * @return the table creation string      */
specifier|public
name|String
name|getTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, DATA varchar(16384), BDATA blob("
operator|+
literal|1024
operator|*
literal|1024
operator|*
literal|1024
operator|+
literal|"))"
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getIndexCreationStatements
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
if|if
condition|(
name|CREATEINDEX
operator|.
name|equals
argument_list|(
literal|"modified-id"
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"create index "
operator|+
name|tableName
operator|+
literal|"_MI on "
operator|+
name|tableName
operator|+
literal|" (MODIFIED, ID)"
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|CREATEINDEX
operator|.
name|equals
argument_list|(
literal|"id-modified"
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"create index "
operator|+
name|tableName
operator|+
literal|"_MI on "
operator|+
name|tableName
operator|+
literal|" (ID, MODIFIED)"
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|CREATEINDEX
operator|.
name|equals
argument_list|(
literal|"modified"
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"create index "
operator|+
name|tableName
operator|+
literal|"_MI on "
operator|+
name|tableName
operator|+
literal|" (MODIFIED)"
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
specifier|public
name|String
name|getAdditionalDiagnostics
parameter_list|(
name|RDBConnectionHandler
name|ch
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
specifier|protected
name|String
name|description
decl_stmt|;
specifier|private
name|RDBDocumentStoreDB
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|description
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|RDBDocumentStoreDB
name|getValue
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
for|for
control|(
name|RDBDocumentStoreDB
name|db
range|:
name|RDBDocumentStoreDB
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|db
operator|.
name|description
operator|.
name|equals
argument_list|(
name|desc
argument_list|)
condition|)
block|{
return|return
name|db
return|;
block|}
elseif|else
if|if
condition|(
name|db
operator|==
name|DB2
operator|&&
name|desc
operator|.
name|startsWith
argument_list|(
literal|"DB2/"
argument_list|)
condition|)
block|{
return|return
name|db
return|;
block|}
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"DB type "
operator|+
name|desc
operator|+
literal|" unknown, trying default settings"
argument_list|)
expr_stmt|;
name|DEFAULT
operator|.
name|description
operator|=
name|desc
operator|+
literal|" - using default settings"
expr_stmt|;
return|return
name|DEFAULT
return|;
block|}
block|}
end_enum

end_unit

