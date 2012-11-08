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
name|nodetype
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|JcrConstants
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
name|api
operator|.
name|CommitFailedException
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
name|api
operator|.
name|Type
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
name|core
operator|.
name|RootImpl
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
name|memory
operator|.
name|PropertyStates
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|security
operator|.
name|user
operator|.
name|UserConstants
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
name|NodeBuilder
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
name|NodeStoreBranch
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
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  * {@code InitialContent} implements a {@link RepositoryInitializer} and  * registers built-in node types when the micro kernel becomes available.  */
end_comment

begin_class
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
name|RepositoryInitializer
operator|.
name|class
argument_list|)
specifier|public
class|class
name|InitialContent
implements|implements
name|RepositoryInitializer
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|NodeStoreBranch
name|branch
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|NodeBuilder
name|root
init|=
name|branch
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:root"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|root
operator|.
name|hasChildNode
argument_list|(
literal|"jcr:system"
argument_list|)
condition|)
block|{
name|NodeBuilder
name|system
init|=
name|root
operator|.
name|child
argument_list|(
literal|"jcr:system"
argument_list|)
decl_stmt|;
name|system
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:system"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|system
operator|.
name|child
argument_list|(
literal|"jcr:versionStorage"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:versionStorage"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|system
operator|.
name|child
argument_list|(
literal|"jcr:nodeTypes"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:nodeTypes"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|system
operator|.
name|child
argument_list|(
literal|"jcr:activities"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"rep:Activities"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|root
operator|.
name|hasChildNode
argument_list|(
literal|"oak:index"
argument_list|)
condition|)
block|{
name|NodeBuilder
name|index
init|=
name|root
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
decl_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"uuid"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"property"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
literal|"jcr:uuid"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"unique"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"nodetype"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"property"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
comment|// FIXME: user-mgt related unique properties (rep:authorizableId, rep:principalName) are implementation detail and not generic for repo
comment|// FIXME OAK-396: rep:principalName only needs to be unique if defined with user/group nodes -> add defining nt-info to uniqueness constraint otherwise ac-editing will fail.
name|index
operator|.
name|child
argument_list|(
literal|"authorizableId"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"property"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"unique"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"principalName"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"property"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"unique"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"members"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"property"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|branch
operator|.
name|setRoot
argument_list|(
name|root
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// TODO: shouldn't need the wrapper
block|}
name|BuiltInNodeTypes
operator|.
name|register
argument_list|(
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

