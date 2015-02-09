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
name|util
operator|.
name|Locale
import|;
end_import

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
block|}
end_class

end_unit

