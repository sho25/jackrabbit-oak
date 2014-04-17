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
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
parameter_list|)
block|{
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
literal|"Getting Dricer for "
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
name|MicroKernelException
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * A {@link Closeable} {@link DataSource} based on a {@link BasicDataSource}.       */
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

