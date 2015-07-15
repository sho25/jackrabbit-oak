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
name|SQLException
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
block|}
end_class

end_unit

