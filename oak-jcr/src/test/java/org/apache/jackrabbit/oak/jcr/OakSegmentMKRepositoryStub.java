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
name|security
operator|.
name|Principal
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
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
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
name|javax
operator|.
name|jcr
operator|.
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|Mongo
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
name|api
operator|.
name|JackrabbitSession
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalIterator
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalManager
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
name|Oak
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
name|segment
operator|.
name|SegmentNodeStore
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
name|segment
operator|.
name|mongo
operator|.
name|MongoStore
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
name|test
operator|.
name|NotExecutableException
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
name|test
operator|.
name|RepositoryStub
import|;
end_import

begin_comment
comment|/**  * A repository stub implementation for Oak on MongoMK  */
end_comment

begin_class
specifier|public
class|class
name|OakSegmentMKRepositoryStub
extends|extends
name|RepositoryStub
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|HOST
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.host"
argument_list|,
literal|"127.0.0.1"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|PORT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"mongo.port"
argument_list|,
literal|27017
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|DB
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"segment.db"
argument_list|,
literal|"SegmentMK"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Mongo
name|connection
decl_stmt|;
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
comment|/**      * Constructor as required by the JCR TCK.      *      * @param settings repository settings      * @throws javax.jcr.RepositoryException If an error occurs.      */
specifier|public
name|OakSegmentMKRepositoryStub
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
name|connection
operator|=
operator|new
name|Mongo
argument_list|(
name|HOST
argument_list|,
name|PORT
argument_list|)
expr_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
operator|new
name|Oak
argument_list|(
operator|new
name|SegmentNodeStore
argument_list|(
operator|new
name|MongoStore
argument_list|(
name|connection
operator|.
name|getDB
argument_list|(
name|DB
argument_list|)
argument_list|,
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|repository
operator|=
name|jcr
operator|.
name|createRepository
argument_list|()
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
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|boolean
name|isAvailable
parameter_list|()
block|{
try|try
block|{
name|Mongo
name|mongo
init|=
operator|new
name|Mongo
argument_list|(
name|HOST
argument_list|,
name|PORT
argument_list|)
decl_stmt|;
try|try
block|{
name|mongo
operator|.
name|getDatabaseNames
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|mongo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Returns the configured repository instance.      *      * @return the configured repository instance.      */
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
annotation|@
name|Override
specifier|public
name|Credentials
name|getReadOnlyCredentials
parameter_list|()
block|{
return|return
operator|new
name|GuestCredentials
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Principal
name|getKnownPrincipal
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|session
operator|instanceof
name|JackrabbitSession
condition|)
block|{
name|PrincipalIterator
name|principals
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|session
operator|)
operator|.
name|getPrincipalManager
argument_list|()
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
decl_stmt|;
if|if
condition|(
name|principals
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|principals
operator|.
name|nextPrincipal
argument_list|()
return|;
block|}
block|}
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
specifier|private
specifier|static
specifier|final
name|Principal
name|UNKNOWN_PRINCIPAL
init|=
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"an_unknown_user"
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Principal
name|getUnknownPrincipal
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
return|return
name|UNKNOWN_PRINCIPAL
return|;
block|}
block|}
end_class

end_unit

