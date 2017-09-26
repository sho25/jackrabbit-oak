begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|fixture
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
name|File
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
name|lang
operator|.
name|StringUtils
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
name|DocumentMK
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
name|DocumentNodeStore
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
name|RDBDataSourceFactory
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
name|RDBOptions
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_class
specifier|public
class|class
name|DocumentRdbFixture
extends|extends
name|NodeStoreFixture
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|NodeStore
argument_list|,
name|DataSource
argument_list|>
name|dataSources
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|NodeStore
argument_list|,
name|DataSource
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|jdbcUrl
decl_stmt|;
specifier|private
specifier|final
name|String
name|fname
init|=
operator|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
operator|)
operator|.
name|isDirectory
argument_list|()
condition|?
literal|"target/"
else|:
literal|""
decl_stmt|;
specifier|private
specifier|final
name|String
name|pUrl
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-url"
argument_list|,
literal|"jdbc:h2:file:./{fname}oaktest"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|pUser
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-user"
argument_list|,
literal|"sa"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|pPasswd
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-passwd"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|()
block|{
name|String
name|prefix
init|=
literal|"T"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|RDBOptions
name|options
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
name|prefix
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|this
operator|.
name|jdbcUrl
operator|=
name|pUrl
operator|.
name|replace
argument_list|(
literal|"{fname}"
argument_list|,
name|fname
argument_list|)
expr_stmt|;
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|jdbcUrl
argument_list|,
name|pUser
argument_list|,
name|pPasswd
argument_list|)
decl_stmt|;
name|NodeStore
name|result
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setPersistentCache
argument_list|(
literal|"target/persistentCache,time"
argument_list|)
operator|.
name|setRDBConnection
argument_list|(
name|ds
argument_list|,
name|options
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|this
operator|.
name|dataSources
operator|.
name|put
argument_list|(
name|result
argument_list|,
name|ds
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
if|if
condition|(
name|nodeStore
operator|instanceof
name|DocumentNodeStore
condition|)
block|{
operator|(
operator|(
name|DocumentNodeStore
operator|)
name|nodeStore
operator|)
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|DataSource
name|ds
init|=
name|this
operator|.
name|dataSources
operator|.
name|remove
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
if|if
condition|(
name|ds
operator|instanceof
name|Closeable
condition|)
block|{
try|try
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
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
literal|"DocumentNodeStore[RDB] on "
operator|+
name|StringUtils
operator|.
name|defaultString
argument_list|(
name|this
operator|.
name|jdbcUrl
argument_list|,
name|this
operator|.
name|pUrl
argument_list|)
return|;
block|}
block|}
end_class

end_unit
