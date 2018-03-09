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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|cycle
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|limit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|ResultSetMetaData
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
name|sql
operator|.
name|Types
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
name|Collection
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
name|Locale
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|DocumentStoreException
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
name|util
operator|.
name|UTF8Encoder
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_comment
comment|/**  * Convenience methods dealing with JDBC specifics.  */
end_comment

begin_class
specifier|public
class|class
name|RDBJDBCTools
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RDBJDBCTools
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|String
name|jdbctype
parameter_list|(
name|String
name|jdbcurl
parameter_list|)
block|{
if|if
condition|(
name|jdbcurl
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|String
name|t
init|=
name|jdbcurl
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|t
operator|.
name|startsWith
argument_list|(
literal|"jdbc:"
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|t
operator|=
name|t
operator|.
name|substring
argument_list|(
literal|"jbdc:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|p
init|=
name|t
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|<=
literal|0
condition|)
block|{
return|return
name|t
return|;
block|}
else|else
block|{
return|return
name|t
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
return|;
block|}
block|}
block|}
block|}
specifier|protected
specifier|static
name|String
name|driverForDBType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
literal|"h2"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|"org.h2.Driver"
return|;
block|}
elseif|else
if|if
condition|(
literal|"derby"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|"org.apache.derby.jdbc.EmbeddedDriver"
return|;
block|}
elseif|else
if|if
condition|(
literal|"postgresql"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|"org.postgresql.Driver"
return|;
block|}
elseif|else
if|if
condition|(
literal|"db2"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|"com.ibm.db2.jcc.DB2Driver"
return|;
block|}
elseif|else
if|if
condition|(
literal|"mysql"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|"com.mysql.jdbc.Driver"
return|;
block|}
elseif|else
if|if
condition|(
literal|"oracle"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|"oracle.jdbc.OracleDriver"
return|;
block|}
elseif|else
if|if
condition|(
literal|"sqlserver"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|"com.microsoft.sqlserver.jdbc.SQLServerDriver"
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
specifier|private
specifier|static
annotation|@
name|Nonnull
name|String
name|checkLegalTableName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|tableName
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tableName
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|tableName
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
operator|(
name|c
operator|>=
literal|'a'
operator|&&
name|c
operator|<=
literal|'z'
operator|)
operator|||
operator|(
name|c
operator|>=
literal|'A'
operator|&&
name|c
operator|<=
literal|'Z'
operator|)
operator|||
operator|(
name|c
operator|>=
literal|'0'
operator|&&
name|c
operator|<=
literal|'9'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'_'
operator|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid character '"
operator|+
name|c
operator|+
literal|"' in table name '"
operator|+
name|tableName
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
return|return
name|tableName
return|;
block|}
comment|/**      * Creates a table name based on an optional prefix and a base name.      *       * @throws IllegalArgumentException      *             upon illegal characters in name      */
specifier|protected
specifier|static
annotation|@
name|Nonnull
name|String
name|createTableName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|prefix
parameter_list|,
annotation|@
name|Nonnull
name|String
name|basename
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|String
name|p
init|=
name|checkLegalTableName
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|String
name|b
init|=
name|checkLegalTableName
argument_list|(
name|basename
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
operator|!
name|p
operator|.
name|endsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
name|p
operator|+=
literal|"_"
expr_stmt|;
block|}
return|return
name|p
operator|+
name|b
return|;
block|}
comment|/**      * Return string representation of transaction isolation level.      */
specifier|protected
specifier|static
annotation|@
name|Nonnull
name|String
name|isolationLevelToString
parameter_list|(
name|int
name|isolationLevel
parameter_list|)
block|{
name|String
name|name
decl_stmt|;
switch|switch
condition|(
name|isolationLevel
condition|)
block|{
case|case
name|Connection
operator|.
name|TRANSACTION_NONE
case|:
name|name
operator|=
literal|"TRANSACTION_NONE"
expr_stmt|;
break|break;
case|case
name|Connection
operator|.
name|TRANSACTION_READ_COMMITTED
case|:
name|name
operator|=
literal|"TRANSACTION_READ_COMMITTED"
expr_stmt|;
break|break;
case|case
name|Connection
operator|.
name|TRANSACTION_READ_UNCOMMITTED
case|:
name|name
operator|=
literal|"TRANSACTION_READ_UNCOMMITTED"
expr_stmt|;
break|break;
case|case
name|Connection
operator|.
name|TRANSACTION_REPEATABLE_READ
case|:
name|name
operator|=
literal|"TRANSACTION_REPEATABLE_READ"
expr_stmt|;
break|break;
case|case
name|Connection
operator|.
name|TRANSACTION_SERIALIZABLE
case|:
name|name
operator|=
literal|"TRANSACTION_SERIALIZABLE"
expr_stmt|;
break|break;
default|default:
name|name
operator|=
literal|"unknown"
expr_stmt|;
break|break;
block|}
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%d)"
argument_list|,
name|name
argument_list|,
name|isolationLevel
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|dumpColumnMeta
parameter_list|(
name|String
name|columnName
parameter_list|,
name|int
name|type
parameter_list|,
name|String
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
name|boolean
name|skipPrecision
init|=
name|precision
operator|==
literal|0
operator|||
operator|(
name|type
operator|==
name|Types
operator|.
name|SMALLINT
operator|&&
name|precision
operator|==
literal|5
operator|)
operator|||
operator|(
name|type
operator|==
name|Types
operator|.
name|BIGINT
operator|&&
name|precision
operator|==
literal|19
operator|)
decl_stmt|;
return|return
name|skipPrecision
condition|?
name|String
operator|.
name|format
argument_list|(
literal|"%s %s"
argument_list|,
name|columnName
argument_list|,
name|typeName
argument_list|)
else|:
name|String
operator|.
name|format
argument_list|(
literal|"%s %s(%d)"
argument_list|,
name|columnName
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|)
return|;
block|}
comment|/**      * Return approximated string representation of table DDL.      */
specifier|protected
specifier|static
name|String
name|dumpResultSetMeta
parameter_list|(
name|ResultSetMetaData
name|met
parameter_list|)
block|{
try|try
block|{
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
name|String
operator|.
name|format
argument_list|(
literal|"%s.%s: "
argument_list|,
name|met
operator|.
name|getSchemaName
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|,
name|met
operator|.
name|getTableName
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|types
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|met
operator|.
name|getColumnCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|dumpColumnMeta
argument_list|(
name|met
operator|.
name|getColumnName
argument_list|(
name|i
argument_list|)
argument_list|,
name|met
operator|.
name|getColumnType
argument_list|(
name|i
argument_list|)
argument_list|,
name|met
operator|.
name|getColumnTypeName
argument_list|(
name|i
argument_list|)
argument_list|,
name|met
operator|.
name|getPrecision
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|put
argument_list|(
name|met
operator|.
name|getColumnTypeName
argument_list|(
name|i
argument_list|)
argument_list|,
name|met
operator|.
name|getColumnType
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" /* "
operator|+
name|types
operator|.
name|toString
argument_list|()
operator|+
literal|" */"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
return|return
literal|"Column metadata unavailable: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
comment|/**      * Return a string containing additional messages from chained exceptions.      */
specifier|protected
specifier|static
annotation|@
name|Nonnull
name|String
name|getAdditionalMessages
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|messages
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|message
init|=
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|SQLException
name|next
init|=
name|ex
operator|.
name|getNextException
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|String
name|m
init|=
name|next
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|message
operator|.
name|equals
argument_list|(
name|m
argument_list|)
condition|)
block|{
name|messages
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|next
operator|=
name|next
operator|.
name|getNextException
argument_list|()
expr_stmt|;
block|}
return|return
name|messages
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
name|messages
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Check whether the exception matches one of the given states.      */
specifier|protected
specifier|static
name|boolean
name|matchesSQLState
parameter_list|(
name|SQLException
name|ex
parameter_list|,
name|String
modifier|...
name|statePrefix
parameter_list|)
block|{
name|String
name|state
init|=
name|ex
operator|.
name|getSQLState
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|sp
range|:
name|statePrefix
control|)
block|{
if|if
condition|(
name|state
operator|.
name|startsWith
argument_list|(
name|sp
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Generate database + driver version diagnostics.      *       * @param md      *            metadata object      * @param dbmax      *            minimal DB major version number (where {@code -1} disables the      *            check)      * @param dbmin      *            minimal DB minor version number      * @param drmax      *            minimal driver major version number (where {@code -1} disables      *            the check)      * @param drmin      *            minimal driver minor version number      * @param dbname      *            database type      * @return diagnostics (empty when there's nothing to complain about)      */
specifier|protected
specifier|static
name|String
name|versionCheck
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|,
name|int
name|dbmax
parameter_list|,
name|int
name|dbmin
parameter_list|,
name|int
name|drmax
parameter_list|,
name|int
name|drmin
parameter_list|,
name|String
name|dbname
parameter_list|)
throws|throws
name|SQLException
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|dbmax
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|maj
init|=
name|md
operator|.
name|getDatabaseMajorVersion
argument_list|()
decl_stmt|;
name|int
name|min
init|=
name|md
operator|.
name|getDatabaseMinorVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|maj
operator|<
name|dbmax
operator|||
operator|(
name|maj
operator|==
name|dbmax
operator|&&
name|min
operator|<
name|dbmin
operator|)
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"Unsupported "
operator|+
name|dbname
operator|+
literal|" version: "
operator|+
name|maj
operator|+
literal|"."
operator|+
name|min
operator|+
literal|", expected at least "
operator|+
name|dbmax
operator|+
literal|"."
operator|+
name|dbmin
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|drmax
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|maj
init|=
name|md
operator|.
name|getDriverMajorVersion
argument_list|()
decl_stmt|;
name|int
name|min
init|=
name|md
operator|.
name|getDriverMinorVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|maj
operator|<
name|drmax
operator|||
operator|(
name|maj
operator|==
name|drmax
operator|&&
name|min
operator|<
name|drmin
operator|)
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|"Unsupported "
operator|+
name|dbname
operator|+
literal|" driver version: "
operator|+
name|md
operator|.
name|getDriverName
argument_list|()
operator|+
literal|" "
operator|+
name|maj
operator|+
literal|"."
operator|+
name|min
operator|+
literal|", expected at least "
operator|+
name|drmax
operator|+
literal|"."
operator|+
name|drmin
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Generate database version diagnostics.      *       * @param md      *            metadata object      * @param dbmax      *            minimal DB major version number (where {@code -1} disables the      *            check)      * @param dbmin      *            minimal DB minor version number      * @param dbname      *            database type      * @return diagnostics (empty when there's nothing to complain about)      */
specifier|protected
specifier|static
name|String
name|versionCheck
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|,
name|int
name|dbmax
parameter_list|,
name|int
name|dbmin
parameter_list|,
name|String
name|dbname
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|versionCheck
argument_list|(
name|md
argument_list|,
name|dbmax
argument_list|,
name|dbmin
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|dbname
argument_list|)
return|;
block|}
comment|/**      * Closes a {@link Statement}, logging potential problems.      * @return null      */
specifier|protected
specifier|static
parameter_list|<
name|T
extends|extends
name|Statement
parameter_list|>
name|T
name|closeStatement
parameter_list|(
annotation|@
name|CheckForNull
name|T
name|stmt
parameter_list|)
block|{
if|if
condition|(
name|stmt
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stmt
operator|.
name|close
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
literal|"Closing statement"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Closes a {@link ResultSet}, logging potential problems.      * @return null      */
specifier|protected
specifier|static
name|ResultSet
name|closeResultSet
parameter_list|(
annotation|@
name|CheckForNull
name|ResultSet
name|rs
parameter_list|)
block|{
if|if
condition|(
name|rs
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|rs
operator|.
name|close
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
literal|"Closing result set"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Provides a component for a {@link PreparedStatement} and a method for      * setting the parameters within this component      */
specifier|public
interface|interface
name|PreparedStatementComponent
block|{
comment|/**          * @return a string suitable for inclusion into a          *         {@link PreparedStatement}          */
annotation|@
name|Nonnull
specifier|public
name|String
name|getStatementComponent
parameter_list|()
function_decl|;
comment|/**          * Set the parameters need by the statement component returned by          * {@link #getStatementComponent()}          *           * @param stmt          *            the statement          * @param startIndex          *            of first parameter to set          * @return index of next parameter to set          * @throws SQLException          */
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
function_decl|;
block|}
comment|/**      * Appends following SQL condition to the builder: {@code ID in (?,?,?)}.      * The field name {@code ID} and the number of place holders is      * configurable. If the number of place holders is greater than      * {@code maxListLength}, then the condition will have following form:      * {@code (ID in (?,?,?) or ID in (?,?,?) or ID in (?,?))}      *      * @param builder      *            the condition will be appended here      * @param field      *            name of the field      * @param placeholdersCount      *            how many ? should be included      * @param maxListLength      *            what's the max number of ? in one list      */
specifier|protected
specifier|static
name|void
name|appendInCondition
parameter_list|(
name|StringBuilder
name|builder
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|placeholdersCount
parameter_list|,
name|int
name|maxListLength
parameter_list|)
block|{
if|if
condition|(
name|placeholdersCount
operator|==
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|" = ?"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|placeholdersCount
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|placeholdersCount
operator|>
name|maxListLength
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|placeholdersCount
operator|/
name|maxListLength
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" or "
argument_list|)
expr_stmt|;
block|}
name|appendInCondition
argument_list|(
name|builder
argument_list|,
name|field
argument_list|,
name|maxListLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|placeholdersCount
operator|%
name|maxListLength
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" or "
argument_list|)
expr_stmt|;
block|}
name|appendInCondition
argument_list|(
name|builder
argument_list|,
name|field
argument_list|,
name|placeholdersCount
operator|%
name|maxListLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|placeholdersCount
operator|>
name|maxListLength
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|appendInCondition
parameter_list|(
name|StringBuilder
name|builder
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|placeholdersCount
parameter_list|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|" in ("
argument_list|)
expr_stmt|;
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|appendTo
argument_list|(
name|builder
argument_list|,
name|limit
argument_list|(
name|cycle
argument_list|(
literal|'?'
argument_list|)
argument_list|,
name|placeholdersCount
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
comment|// see<https://issues.apache.org/jira/browse/OAK-3843>
specifier|public
specifier|static
specifier|final
name|int
name|MAX_IN_CLAUSE
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"org.apache.jackrabbit.oak.plugins.document.rdb.RDBJDBCTools.MAX_IN_CLAUSE"
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|PreparedStatementComponent
name|createInStatement
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
specifier|final
name|boolean
name|binary
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|>
name|MAX_IN_CLAUSE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Maximum size of IN clause allowed is "
operator|+
name|MAX_IN_CLAUSE
operator|+
literal|", but "
operator|+
name|values
operator|.
name|size
argument_list|()
operator|+
literal|" was requested"
argument_list|)
throw|;
block|}
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|values
operator|.
name|size
argument_list|()
operator|*
literal|3
argument_list|)
decl_stmt|;
comment|// maximum "in" statement in Oracle takes 1000 values
name|appendInCondition
argument_list|(
name|sb
argument_list|,
name|fieldName
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
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
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
try|try
block|{
if|if
condition|(
name|binary
condition|)
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
name|startIndex
operator|++
argument_list|,
name|UTF8Encoder
operator|.
name|encodeAsByteArray
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|UTF8Encoder
operator|.
name|canEncode
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"can not encode as UTF-8"
argument_list|)
throw|;
block|}
name|stmt
operator|.
name|setString
argument_list|(
name|startIndex
operator|++
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid ID: "
operator|+
name|value
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|asDocumentStoreException
argument_list|(
name|ex
argument_list|,
literal|"Invalid ID: "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
return|return
name|startIndex
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|DocumentStoreException
name|asDocumentStoreException
parameter_list|(
annotation|@
name|Nonnull
name|Exception
name|cause
parameter_list|,
annotation|@
name|Nonnull
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|DocumentStoreException
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
return|;
block|}
block|}
end_class

end_unit

