begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|mongo
operator|.
name|MongoDockerRule
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
name|MongoConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExternalResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|MongoConnectionFactory
extends|extends
name|ExternalResource
block|{
specifier|private
specifier|final
name|MongoDockerRule
name|mongo
init|=
operator|new
name|MongoDockerRule
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|MongoConnection
argument_list|>
name|connections
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Statement
name|apply
parameter_list|(
name|Statement
name|base
parameter_list|,
name|Description
name|description
parameter_list|)
block|{
name|Statement
name|s
init|=
name|super
operator|.
name|apply
argument_list|(
name|base
argument_list|,
name|description
argument_list|)
decl_stmt|;
if|if
condition|(
name|mongo
operator|.
name|isDockerAvailable
argument_list|()
condition|)
block|{
name|s
operator|=
name|mongo
operator|.
name|apply
argument_list|(
name|s
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|MongoConnection
name|getConnection
parameter_list|()
block|{
return|return
name|getConnection
argument_list|(
name|MongoUtils
operator|.
name|DB
argument_list|)
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|MongoConnection
name|getConnection
parameter_list|(
name|String
name|dbName
parameter_list|)
block|{
comment|// first try MongoDB running on configured host and port
name|MongoConnection
name|c
init|=
name|MongoUtils
operator|.
name|getConnection
argument_list|(
name|dbName
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
operator|&&
name|mongo
operator|.
name|isDockerAvailable
argument_list|()
condition|)
block|{
comment|// fall back to docker if available
name|c
operator|=
operator|new
name|MongoConnection
argument_list|(
literal|"localhost"
argument_list|,
name|mongo
operator|.
name|getPort
argument_list|()
argument_list|,
name|dbName
argument_list|)
expr_stmt|;
block|}
name|assumeNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|connections
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|after
parameter_list|()
block|{
for|for
control|(
name|MongoConnection
name|c
range|:
name|connections
control|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// may happen when connection is already closed (OAK-7447)
block|}
block|}
block|}
block|}
end_class

end_unit

