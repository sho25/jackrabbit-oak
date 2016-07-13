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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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

begin_comment
comment|/**  * A wrapper for {@link DataSource} that offers logging of various  * operations.  *<p>  * Note that the implementations currently focus on method invocations done  * by {@link RDBDocumentStore} and thus may not be applicable for other use cases.  */
end_comment

begin_class
specifier|public
class|class
name|RDBDataSourceWrapper
implements|implements
name|DataSource
implements|,
name|Closeable
block|{
comment|// sample use in BasicDocumentStoreTest:
comment|// to start
comment|// if (super.rdbDataSource != null) {
comment|// ((RDBDataSourceWrapper) super.rdbDataSource).startLog();
comment|// }
comment|// to dump
comment|// if (super.rdbDataSource != null) {
comment|// RDBLogEntry.DUMP(System.err, ((RDBDataSourceWrapper)
comment|// super.rdbDataSource).stopLog());
comment|// }
specifier|private
specifier|final
name|DataSource
name|ds
decl_stmt|;
specifier|private
name|boolean
name|batchResultPrecise
init|=
literal|true
decl_stmt|;
comment|// Logging
specifier|private
name|Map
argument_list|<
name|Thread
argument_list|,
name|List
argument_list|<
name|RDBLogEntry
argument_list|>
argument_list|>
name|loggerMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Thread
argument_list|,
name|List
argument_list|<
name|RDBLogEntry
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|RDBLogEntry
argument_list|>
name|getLog
parameter_list|()
block|{
return|return
name|loggerMap
operator|.
name|get
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|void
name|startLog
parameter_list|(
name|Thread
name|thread
parameter_list|)
block|{
name|loggerMap
operator|.
name|put
argument_list|(
name|thread
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|RDBLogEntry
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Start logging for the current thread.      */
specifier|public
name|void
name|startLog
parameter_list|()
block|{
name|startLog
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|RDBLogEntry
argument_list|>
name|stopLog
parameter_list|(
name|Thread
name|thread
parameter_list|)
block|{
return|return
name|loggerMap
operator|.
name|remove
argument_list|(
name|thread
argument_list|)
return|;
block|}
comment|/**      * End logging for the current thread and obtain log results.      */
specifier|public
name|List
argument_list|<
name|RDBLogEntry
argument_list|>
name|stopLog
parameter_list|()
block|{
return|return
name|stopLog
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Set to {@code false} to simulate drivers/DBs that do not return the number of affected rows in {@link Statement#executeBatch()}.      */
specifier|public
name|void
name|setBatchResultPrecise
parameter_list|(
name|boolean
name|precise
parameter_list|)
block|{
name|this
operator|.
name|batchResultPrecise
operator|=
name|precise
expr_stmt|;
block|}
specifier|public
name|boolean
name|isBatchResultPrecise
parameter_list|()
block|{
return|return
name|this
operator|.
name|batchResultPrecise
return|;
block|}
comment|// DataSource
specifier|public
name|RDBDataSourceWrapper
parameter_list|(
name|DataSource
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
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|SQLException
block|{
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|RDBConnectionWrapper
argument_list|(
name|this
argument_list|,
name|ds
operator|.
name|getConnection
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|List
argument_list|<
name|RDBLogEntry
argument_list|>
name|l
init|=
name|getLog
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|l
operator|.
name|add
argument_list|(
operator|new
name|RDBLogEntry
argument_list|(
name|start
argument_list|,
literal|"got connection"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Connection
name|getConnection
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|ds
operator|.
name|getConnection
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
return|;
block|}
specifier|public
name|PrintWriter
name|getLogWriter
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|ds
operator|.
name|getLogWriter
argument_list|()
return|;
block|}
specifier|public
name|int
name|getLoginTimeout
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|ds
operator|.
name|getLoginTimeout
argument_list|()
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
specifier|public
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|ds
operator|.
name|isWrapperFor
argument_list|(
name|iface
argument_list|)
return|;
block|}
specifier|public
name|void
name|setLogWriter
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
throws|throws
name|SQLException
block|{
name|ds
operator|.
name|setLogWriter
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setLoginTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|SQLException
block|{
name|ds
operator|.
name|setLoginTimeout
argument_list|(
name|seconds
argument_list|)
expr_stmt|;
block|}
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
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|ds
operator|.
name|unwrap
argument_list|(
name|iface
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
if|if
condition|(
name|ds
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|ds
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
end_class

end_unit

