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
name|jcr
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
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|query
operator|.
name|QueryEngineSettings
import|;
end_import

begin_comment
comment|/**  * A repository stub implementation for the RDB document store.  */
end_comment

begin_class
specifier|public
class|class
name|OakDocumentRDBRepositoryStub
extends|extends
name|OakRepositoryStub
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|URL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-url"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|// such as: jdbc:h2:mem:oaknodes
specifier|protected
specifier|static
specifier|final
name|String
name|USERNAME
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
specifier|protected
specifier|static
specifier|final
name|String
name|PASSWD
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
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
comment|/**      * Constructor as required by the JCR TCK.      *       * @param settings      *            repository settings      * @throws javax.jcr.RepositoryException      *             If an error occurs.      */
specifier|public
name|OakDocumentRDBRepositoryStub
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
try|try
block|{
name|this
operator|.
name|repository
operator|=
name|createRepository
argument_list|(
name|URL
argument_list|,
name|USERNAME
argument_list|,
name|PASSWD
argument_list|)
expr_stmt|;
name|session
operator|=
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|superuser
argument_list|)
expr_stmt|;
name|TestContentLoader
name|loader
init|=
operator|new
name|TestContentLoader
argument_list|()
decl_stmt|;
name|loader
operator|.
name|loadTestContent
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(connection)));
block|}
specifier|protected
name|Repository
name|createRepository
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|DocumentNodeStore
name|m
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
literal|64
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
operator|.
name|setPersistentCache
argument_list|(
literal|"target/persistentCache,time"
argument_list|)
operator|.
name|setRDBConnection
argument_list|(
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|url
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|QueryEngineSettings
name|qs
init|=
operator|new
name|QueryEngineSettings
argument_list|()
decl_stmt|;
name|qs
operator|.
name|setFullTextComparisonWithoutIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|Jcr
argument_list|(
name|m
argument_list|)
operator|.
name|with
argument_list|(
name|qs
argument_list|)
operator|.
name|createRepository
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isAvailable
parameter_list|()
block|{
try|try
block|{
name|Connection
name|c
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|URL
argument_list|,
name|USERNAME
argument_list|,
name|PASSWD
argument_list|)
decl_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
comment|// expected
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Returns the configured repository instance.      *       * @return the configured repository instance.      */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|repository
return|;
block|}
block|}
end_class

end_unit

