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
name|io
operator|.
name|Closeable
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
name|io
operator|.
name|PrintWriter
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
name|java
operator|.
name|sql
operator|.
name|SQLFeatureNotSupportedException
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
name|logging
operator|.
name|Logger
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
name|commons
operator|.
name|dbcp
operator|.
name|BasicDataSource
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
name|driverForDBType
argument_list|(
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
name|ex
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
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
name|BasicDataSource
name|bds
init|=
operator|new
name|BasicDataSource
argument_list|()
decl_stmt|;
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
name|bds
operator|.
name|setDriverClassName
argument_list|(
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|bds
operator|.
name|setUsername
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|bds
operator|.
name|setPassword
argument_list|(
name|passwd
argument_list|)
expr_stmt|;
name|bds
operator|.
name|setUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
return|return
operator|new
name|CloseableDataSource
argument_list|(
name|bds
argument_list|)
return|;
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
specifier|private
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
literal|null
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
specifier|private
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
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
comment|/**      * A {@link Closeable} {@link DataSource} based on a {@link BasicDataSource}      * .      */
specifier|private
specifier|static
class|class
name|CloseableDataSource
implements|implements
name|DataSource
implements|,
name|Closeable
block|{
specifier|private
name|BasicDataSource
name|ds
decl_stmt|;
specifier|public
name|CloseableDataSource
parameter_list|(
name|BasicDataSource
name|ds
parameter_list|)
block|{
name|this
operator|.
name|ds
operator|=
name|ds
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|PrintWriter
name|getLogWriter
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|ds
operator|.
name|getLogWriter
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLoginTimeout
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|ds
operator|.
name|getLoginTimeout
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLogWriter
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
throws|throws
name|SQLException
block|{
name|this
operator|.
name|ds
operator|.
name|setLogWriter
argument_list|(
name|pw
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLoginTimeout
parameter_list|(
name|int
name|t
parameter_list|)
throws|throws
name|SQLException
block|{
name|this
operator|.
name|ds
operator|.
name|setLoginTimeout
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|ds
operator|.
name|isWrapperFor
argument_list|(
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|ds
operator|.
name|unwrap
argument_list|(
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|this
operator|.
name|ds
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"closing data source "
operator|+
name|this
operator|.
name|ds
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|ds
operator|.
name|getConnection
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Connection
name|getConnection
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|passwd
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|this
operator|.
name|ds
operator|.
name|getConnection
argument_list|(
name|user
argument_list|,
name|passwd
argument_list|)
return|;
block|}
comment|// needed in Java 7...
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|public
name|Logger
name|getParentLogger
parameter_list|()
throws|throws
name|SQLFeatureNotSupportedException
block|{
throw|throw
operator|new
name|SQLFeatureNotSupportedException
argument_list|()
throw|;
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
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" wrapping a "
operator|+
name|this
operator|.
name|ds
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

