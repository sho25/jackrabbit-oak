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
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ImportUUIDBehavior
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinitionTemplate
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
name|user
operator|.
name|UserManager
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
name|AbstractSecurityTest
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
name|Tree
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
name|spi
operator|.
name|security
operator|.
name|ConfigurationParameters
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
name|UserConfiguration
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
name|xml
operator|.
name|PropInfo
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
name|xml
operator|.
name|ReferenceChangeTracker
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
name|xml
operator|.
name|TextValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|UserImporterTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|UserConstants
block|{
name|UserImporter
name|importer
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|importer
operator|=
operator|new
name|UserImporter
argument_list|(
name|getImportConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
name|ConfigurationParameters
name|getImportConfig
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
name|Session
name|mockJackrabbitSession
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|s
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|JackrabbitSession
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|s
operator|.
name|getUserManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
name|boolean
name|isWorkspaceImport
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|init
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|importer
operator|.
name|init
argument_list|(
name|mockJackrabbitSession
argument_list|()
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|isWorkspaceImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|,
operator|new
name|ReferenceChangeTracker
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Tree
name|createUserTree
parameter_list|()
block|{
name|Tree
name|folder
init|=
name|root
operator|.
name|getTree
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_PATH
argument_list|,
name|DEFAULT_USER_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|userTree
init|=
name|folder
operator|.
name|addChild
argument_list|(
literal|"userTree"
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_USER
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
name|userTree
return|;
block|}
specifier|private
name|PropInfo
name|createPropInfo
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|PropInfo
argument_list|(
name|name
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|,
operator|new
name|TextValue
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|getValue
parameter_list|(
name|int
name|targetType
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getValueFactory
argument_list|(
name|root
argument_list|)
operator|.
name|createValue
argument_list|(
name|value
argument_list|,
name|targetType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|//nop
block|}
block|}
argument_list|)
return|;
block|}
comment|//---------------------------------------------------------------< init>---
annotation|@
name|Test
specifier|public
name|void
name|testInitNoJackrabbitSession
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Session
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|importer
operator|.
name|init
argument_list|(
name|s
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
literal|false
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|,
operator|new
name|ReferenceChangeTracker
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInitAlreadyInitialized
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|()
expr_stmt|;
name|importer
operator|.
name|init
argument_list|(
name|mockJackrabbitSession
argument_list|()
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|isWorkspaceImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|,
operator|new
name|ReferenceChangeTracker
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitImportUUIDBehaviorRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|importer
operator|.
name|init
argument_list|(
name|mockJackrabbitSession
argument_list|()
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|isWorkspaceImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|,
operator|new
name|ReferenceChangeTracker
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitImportUUIDBehaviorReplace
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|importer
operator|.
name|init
argument_list|(
name|mockJackrabbitSession
argument_list|()
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|isWorkspaceImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REPLACE_EXISTING
argument_list|,
operator|new
name|ReferenceChangeTracker
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitImportUUIDBehaviorThrow
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|importer
operator|.
name|init
argument_list|(
name|mockJackrabbitSession
argument_list|()
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|isWorkspaceImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|,
operator|new
name|ReferenceChangeTracker
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitImportUUIDBehaviourCreateNew
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|importer
operator|.
name|init
argument_list|(
name|mockJackrabbitSession
argument_list|()
argument_list|,
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|isWorkspaceImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|,
operator|new
name|ReferenceChangeTracker
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testHandlePropInfoNotInitialized
parameter_list|()
throws|throws
name|Exception
block|{
name|importer
operator|.
name|handlePropInfo
argument_list|(
name|createUserTree
argument_list|()
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|PropInfo
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|PropertyDefinition
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------------------------------< handlePropInfo>---
annotation|@
name|Test
specifier|public
name|void
name|testHandlePropInfoParentNotAuthorizable
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|importer
operator|.
name|handlePropInfo
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|PropInfo
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|PropertyDefinition
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------< processReferences>---
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testProcessReferencesNotInitialized
parameter_list|()
throws|throws
name|Exception
block|{
name|importer
operator|.
name|processReferences
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------------< propertiesCompleted>---
annotation|@
name|Test
specifier|public
name|void
name|testPropertiesCompletedClearsCache
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|userTree
init|=
name|createUserTree
argument_list|()
decl_stmt|;
name|Tree
name|cacheTree
init|=
name|userTree
operator|.
name|addChild
argument_list|(
name|CacheConstants
operator|.
name|REP_CACHE
argument_list|)
decl_stmt|;
name|cacheTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|CacheConstants
operator|.
name|NT_REP_CACHE
argument_list|)
expr_stmt|;
name|importer
operator|.
name|propertiesCompleted
argument_list|(
name|cacheTree
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cacheTree
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|userTree
operator|.
name|hasChild
argument_list|(
name|CacheConstants
operator|.
name|REP_CACHE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertiesCompletedParentNotAuthorizable
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|()
expr_stmt|;
name|importer
operator|.
name|propertiesCompleted
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertiesCompletedMissingId
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|()
expr_stmt|;
name|Tree
name|userTree
init|=
name|createUserTree
argument_list|()
decl_stmt|;
name|importer
operator|.
name|propertiesCompleted
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|userTree
operator|.
name|hasProperty
argument_list|(
name|REP_AUTHORIZABLE_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

