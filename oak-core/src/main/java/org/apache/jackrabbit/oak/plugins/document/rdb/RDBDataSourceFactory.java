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
name|Driver
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Factory for creating {@link DataSource}s based on a JDBC connection URL.  */
end_comment

begin_class
specifier|public
class|class
name|RDBDataSourceFactory
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
name|RDBDataSourceFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|DataSource
name|forJdbcUrl
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|passwd
parameter_list|,
name|String
name|driverName
parameter_list|)
block|{
comment|// load driver class when specified
if|if
condition|(
name|driverName
operator|!=
literal|null
operator|&&
operator|!
name|driverName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"trying to load {}"
argument_list|,
name|driverName
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|driverName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"driver "
operator|+
name|driverName
operator|+
literal|" not loaded"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// try to determine driver from JDBC URL
name|String
name|defaultDriver
init|=
name|RDBJDBCTools
operator|.
name|driverForDBType
argument_list|(
name|RDBJDBCTools
operator|.
name|jdbctype
argument_list|(
name|url
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultDriver
operator|!=
literal|null
operator|&&
operator|!
name|defaultDriver
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"trying to load {}"
argument_list|,
name|defaultDriver
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|defaultDriver
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"driver "
operator|+
name|defaultDriver
operator|+
literal|" not loaded"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Getting driver for "
operator|+
name|url
argument_list|)
expr_stmt|;
name|Driver
name|d
init|=
name|DriverManager
operator|.
name|getDriver
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|String
name|classname
init|=
literal|"org.apache.tomcat.jdbc.pool.DataSource"
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|dsclazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|classname
argument_list|)
decl_stmt|;
name|DataSource
name|ds
init|=
operator|(
name|DataSource
operator|)
name|dsclazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|dsclazz
operator|.
name|getMethod
argument_list|(
literal|"setDriverClassName"
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|invoke
argument_list|(
name|ds
argument_list|,
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|dsclazz
operator|.
name|getMethod
argument_list|(
literal|"setUsername"
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|invoke
argument_list|(
name|ds
argument_list|,
name|username
argument_list|)
expr_stmt|;
name|dsclazz
operator|.
name|getMethod
argument_list|(
literal|"setPassword"
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|invoke
argument_list|(
name|ds
argument_list|,
name|passwd
argument_list|)
expr_stmt|;
name|dsclazz
operator|.
name|getMethod
argument_list|(
literal|"setUrl"
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|invoke
argument_list|(
name|ds
argument_list|,
name|url
argument_list|)
expr_stmt|;
return|return
name|ds
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|String
name|message
init|=
literal|"trying to create datasource "
operator|+
name|classname
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|String
name|message
init|=
literal|"trying to obtain driver for "
operator|+
name|url
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|DataSource
name|forJdbcUrl
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|passwd
parameter_list|)
block|{
return|return
name|forJdbcUrl
argument_list|(
name|url
argument_list|,
name|username
argument_list|,
name|passwd
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

