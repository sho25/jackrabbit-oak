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
name|authorization
operator|.
name|restriction
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
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
name|ImmutableSet
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
name|security
operator|.
name|JackrabbitAccessControlList
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|PropertyState
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
name|plugins
operator|.
name|tree
operator|.
name|TreeUtil
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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|authorization
operator|.
name|restriction
operator|.
name|CompositePattern
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
name|authorization
operator|.
name|restriction
operator|.
name|Restriction
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
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionDefinition
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
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionPattern
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
name|privilege
operator|.
name|PrivilegeConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|assertEquals
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
name|assertNotNull
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
name|assertSame
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
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|mock
import|;
end_import

begin_comment
comment|/**  * Tests for {@link RestrictionProviderImpl}  */
end_comment

begin_class
specifier|public
class|class
name|RestrictionProviderImplTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|AccessControlConstants
block|{
specifier|private
name|RestrictionProviderImpl
name|provider
decl_stmt|;
annotation|@
name|Before
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
name|provider
operator|=
operator|new
name|RestrictionProviderImpl
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetSupportedDefinitions
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|provider
operator|.
name|getSupportedRestrictions
argument_list|(
literal|null
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|RestrictionDefinition
argument_list|>
name|defs
init|=
name|provider
operator|.
name|getSupportedRestrictions
argument_list|(
literal|"/testPath"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|defs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|defs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RestrictionDefinition
name|def
range|:
name|defs
control|)
block|{
if|if
condition|(
name|REP_GLOB
operator|.
name|equals
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|def
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|def
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|REP_NT_NAMES
operator|.
name|equals
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|def
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|def
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|REP_PREFIXES
operator|.
name|equals
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|,
name|def
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|def
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|REP_ITEM_NAMES
operator|.
name|equals
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|def
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|def
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"unexpected restriction "
operator|+
name|def
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRestrictionPattern
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|PropertyState
argument_list|,
name|RestrictionPattern
argument_list|>
name|map
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_GLOB
argument_list|,
literal|"/*/jcr:content"
argument_list|)
argument_list|,
name|GlobPattern
operator|.
name|create
argument_list|(
literal|"/testPath"
argument_list|,
literal|"/*/jcr:content"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ntNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|,
name|JcrConstants
operator|.
name|NT_LINKEDFILE
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|ntNames
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|,
operator|new
name|NodeTypePattern
argument_list|(
name|ntNames
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|TreeUtil
operator|.
name|getOrAddChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"testPath"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|restrictions
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|tree
argument_list|,
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
comment|// test restrictions individually
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PropertyState
argument_list|,
name|RestrictionPattern
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|restrictions
operator|.
name|setProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|RestrictionPattern
name|pattern
init|=
name|provider
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|restrictions
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
name|restrictions
operator|.
name|removeProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test combination on multiple restrictions
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PropertyState
argument_list|,
name|RestrictionPattern
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|restrictions
operator|.
name|setProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RestrictionPattern
name|pattern
init|=
name|provider
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|restrictions
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pattern
operator|instanceof
name|CompositePattern
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPatternForAllSupported
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|PropertyState
argument_list|,
name|RestrictionPattern
argument_list|>
name|map
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_GLOB
argument_list|,
literal|"/*/jcr:content"
argument_list|)
argument_list|,
name|GlobPattern
operator|.
name|create
argument_list|(
literal|"/testPath"
argument_list|,
literal|"/*/jcr:content"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ntNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|,
name|JcrConstants
operator|.
name|NT_LINKEDFILE
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|ntNames
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|,
operator|new
name|NodeTypePattern
argument_list|(
name|ntNames
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"rep"
argument_list|,
literal|"jcr"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_PREFIXES
argument_list|,
name|prefixes
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
operator|new
name|PrefixPattern
argument_list|(
name|prefixes
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|itemNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"abc"
argument_list|,
literal|"jcr:primaryType"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_ITEM_NAMES
argument_list|,
name|prefixes
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|,
operator|new
name|ItemNamePattern
argument_list|(
name|itemNames
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|TreeUtil
operator|.
name|getOrAddChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"testPath"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|restrictions
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|tree
argument_list|,
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PropertyState
argument_list|,
name|RestrictionPattern
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|restrictions
operator|.
name|setProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RestrictionPattern
name|pattern
init|=
name|provider
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|restrictions
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pattern
operator|instanceof
name|CompositePattern
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPatternFromRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|PropertyState
argument_list|,
name|RestrictionPattern
argument_list|>
name|map
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_GLOB
argument_list|,
literal|"/*/jcr:content"
argument_list|)
argument_list|,
name|GlobPattern
operator|.
name|create
argument_list|(
literal|"/testPath"
argument_list|,
literal|"/*/jcr:content"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ntNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|,
name|JcrConstants
operator|.
name|NT_LINKEDFILE
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|ntNames
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|,
operator|new
name|NodeTypePattern
argument_list|(
name|ntNames
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"rep"
argument_list|,
literal|"jcr"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_PREFIXES
argument_list|,
name|prefixes
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
operator|new
name|PrefixPattern
argument_list|(
name|prefixes
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|itemNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"abc"
argument_list|,
literal|"jcr:primaryType"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_ITEM_NAMES
argument_list|,
name|itemNames
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|,
operator|new
name|ItemNamePattern
argument_list|(
name|itemNames
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|TreeUtil
operator|.
name|getOrAddChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"testPath"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|restrictions
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|tree
argument_list|,
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
comment|// test restrictions individually
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PropertyState
argument_list|,
name|RestrictionPattern
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|restrictions
operator|.
name|setProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|RestrictionPattern
name|pattern
init|=
name|provider
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|provider
operator|.
name|readRestrictions
argument_list|(
literal|"/testPath"
argument_list|,
name|tree
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
name|restrictions
operator|.
name|removeProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test combination on multiple restrictions
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PropertyState
argument_list|,
name|RestrictionPattern
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|restrictions
operator|.
name|setProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RestrictionPattern
name|pattern
init|=
name|provider
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|provider
operator|.
name|readRestrictions
argument_list|(
literal|"/testPath"
argument_list|,
name|tree
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pattern
operator|instanceof
name|CompositePattern
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPatternFromTreeNullPath
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|provider
operator|.
name|getPattern
argument_list|(
literal|null
argument_list|,
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPatternFromRestrictionsNullPath
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|provider
operator|.
name|getPattern
argument_list|(
literal|null
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|mock
argument_list|(
name|Restriction
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPatternFromEmptyRestrictions
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|provider
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|ImmutableSet
operator|.
name|of
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
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testValidateGlobRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|TreeUtil
operator|.
name|getOrAddChild
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"testTree"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|t
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|globs
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/1*/2*/3*/4*/5*/6*/7*/8*/9*/10*/11*/12*/13*/14*/15*/16*/17*/18*/19*/20*/21*"
argument_list|,
literal|"*********************"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|glob
range|:
name|globs
control|)
block|{
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acMgr
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|REP_GLOB
argument_list|,
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|glob
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
try|try
block|{
name|provider
operator|.
name|validateRestrictions
argument_list|(
name|path
argument_list|,
name|t
operator|.
name|getChild
argument_list|(
name|REP_POLICY
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"allow"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|acMgr
operator|.
name|removePolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

