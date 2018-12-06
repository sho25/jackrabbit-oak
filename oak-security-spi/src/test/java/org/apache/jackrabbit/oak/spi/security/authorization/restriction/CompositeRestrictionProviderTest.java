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
name|spi
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
name|ArrayList
import|;
end_import

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
name|RepositoryException
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
name|ValueFactory
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
name|ImmutableMap
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
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|value
operator|.
name|jcr
operator|.
name|ValueFactoryImpl
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|CompositeRestrictionProviderTest
implements|implements
name|AccessControlConstants
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NAME_LONGS
init|=
literal|"longs"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NAME_BOOLEAN
init|=
literal|"boolean"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Restriction
name|GLOB_RESTRICTION
init|=
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_GLOB
argument_list|,
literal|"*"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Restriction
name|NT_PREFIXES_RESTRICTION
init|=
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_PREFIXES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Restriction
name|MANDATORY_BOOLEAN_RESTRICTION
init|=
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|NAME_BOOLEAN
argument_list|,
literal|true
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Restriction
name|LONGS_RESTRICTION
init|=
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|NAME_LONGS
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|Type
operator|.
name|LONGS
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Restriction
name|UNKNOWN_RESTRICTION
init|=
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"unknown"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|RestrictionProvider
name|rp1
init|=
operator|new
name|AbstractRestrictionProvider
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|RestrictionDefinition
operator|>
name|of
argument_list|(
name|REP_GLOB
argument_list|,
name|GLOB_RESTRICTION
operator|.
name|getDefinition
argument_list|()
argument_list|,
name|REP_PREFIXES
argument_list|,
name|NT_PREFIXES_RESTRICTION
operator|.
name|getDefinition
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
specifier|private
name|RestrictionProvider
name|rp2
init|=
operator|new
name|AbstractRestrictionProvider
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NAME_BOOLEAN
argument_list|,
name|MANDATORY_BOOLEAN_RESTRICTION
operator|.
name|getDefinition
argument_list|()
argument_list|,
name|NAME_LONGS
argument_list|,
name|LONGS_RESTRICTION
operator|.
name|getDefinition
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|supported
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|MANDATORY_BOOLEAN_RESTRICTION
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|LONGS_RESTRICTION
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|REP_PREFIXES
argument_list|,
name|REP_GLOB
argument_list|)
decl_stmt|;
specifier|private
name|RestrictionProvider
name|provider
init|=
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|rp1
argument_list|,
name|rp2
argument_list|)
decl_stmt|;
specifier|private
name|ValueFactory
name|vf
init|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|private
name|Tree
name|getAceTree
parameter_list|(
name|Restriction
modifier|...
name|restrictions
parameter_list|)
block|{
name|Tree
name|restrictionsTree
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
empty_stmt|;
name|when
argument_list|(
name|restrictionsTree
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|restrictionsTree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PropertyState
argument_list|>
name|properties
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Restriction
name|r
range|:
name|restrictions
control|)
block|{
name|when
argument_list|(
name|restrictionsTree
operator|.
name|getProperty
argument_list|(
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|restrictionsTree
operator|.
name|getProperties
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
name|Iterable
operator|)
name|properties
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|restrictionsTree
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Tree
name|ace
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ace
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_GRANT_ACE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ace
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|restrictionsTree
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ace
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|ace
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionProvider
operator|.
name|EMPTY
argument_list|,
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|Collections
operator|.
expr|<
name|RestrictionProvider
operator|>
name|emptySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingle
parameter_list|()
block|{
name|assertSame
argument_list|(
name|rp1
argument_list|,
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|rp1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNewInstance
parameter_list|()
block|{
name|RestrictionProvider
name|crp
init|=
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|rp1
argument_list|,
name|rp2
argument_list|)
argument_list|)
decl_stmt|;
name|RestrictionProvider
name|crp2
init|=
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|rp1
argument_list|,
name|rp2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|crp
operator|.
name|getSupportedRestrictions
argument_list|(
literal|"/testPath"
argument_list|)
argument_list|,
name|crp2
operator|.
name|getSupportedRestrictions
argument_list|(
literal|"/testPath"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetSupportedRestrictions
parameter_list|()
block|{
name|String
index|[]
name|paths
init|=
operator|new
name|String
index|[]
block|{
literal|null
block|,
literal|"/testPath"
block|}
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
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
name|path
argument_list|)
decl_stmt|;
name|int
name|expectedSize
init|=
name|rp1
operator|.
name|getSupportedRestrictions
argument_list|(
name|path
argument_list|)
operator|.
name|size
argument_list|()
operator|+
name|rp2
operator|.
name|getSupportedRestrictions
argument_list|(
name|path
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
name|defs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|defs
operator|.
name|containsAll
argument_list|(
name|rp1
operator|.
name|getSupportedRestrictions
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|defs
operator|.
name|containsAll
argument_list|(
name|rp2
operator|.
name|getSupportedRestrictions
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|valid
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NAME_BOOLEAN
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
argument_list|,
name|NAME_LONGS
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|10
argument_list|)
argument_list|,
name|REP_GLOB
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"*"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|valid
operator|.
name|keySet
argument_list|()
control|)
block|{
name|provider
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
name|name
argument_list|,
name|valid
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateRestrictionWithInvalidPath
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|provider
operator|.
name|createRestriction
argument_list|(
literal|null
argument_list|,
name|REP_GLOB
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"rep:glob not supported at 'null' path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateInvalidRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|invalid
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NAME_BOOLEAN
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"wrong_type"
argument_list|)
argument_list|,
name|REP_GLOB
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|invalid
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|provider
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
name|name
argument_list|,
name|invalid
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"invalid restriction "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMvCreateRestriction
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Value
index|[]
argument_list|>
name|valid
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NAME_LONGS
argument_list|,
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
literal|100
argument_list|)
block|}
argument_list|,
name|REP_PREFIXES
argument_list|,
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
literal|"prefix"
argument_list|)
block|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"prefix2"
argument_list|)
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|valid
operator|.
name|keySet
argument_list|()
control|)
block|{
name|provider
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
name|name
argument_list|,
name|valid
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|testCreateMvRestrictionWithInvalidPath
parameter_list|()
throws|throws
name|Exception
block|{
name|provider
operator|.
name|createRestriction
argument_list|(
literal|null
argument_list|,
name|REP_PREFIXES
argument_list|,
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
literal|"jcr"
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateInvalidMvRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Value
index|[]
argument_list|>
name|invalid
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NAME_BOOLEAN
argument_list|,
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
block|,
name|vf
operator|.
name|createValue
argument_list|(
literal|false
argument_list|)
block|}
argument_list|,
name|NAME_LONGS
argument_list|,
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
literal|"wrong_type"
argument_list|)
block|}
argument_list|,
name|REP_PREFIXES
argument_list|,
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|invalid
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|provider
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
name|name
argument_list|,
name|invalid
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"invalid restriction "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadRestrictions
parameter_list|()
block|{
name|Tree
name|aceTree
init|=
name|getAceTree
argument_list|(
name|NT_PREFIXES_RESTRICTION
argument_list|,
name|MANDATORY_BOOLEAN_RESTRICTION
argument_list|,
name|UNKNOWN_RESTRICTION
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
init|=
name|provider
operator|.
name|readRestrictions
argument_list|(
literal|"/test"
argument_list|,
name|aceTree
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|restrictions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Restriction
name|r
range|:
name|restrictions
control|)
block|{
name|String
name|name
init|=
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|supported
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"read unsupported restriction"
argument_list|)
expr_stmt|;
block|}
block|}
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
name|testValidateRestrictionsMissingMandatory
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|aceTree
init|=
name|getAceTree
argument_list|(
name|GLOB_RESTRICTION
argument_list|)
decl_stmt|;
name|provider
operator|.
name|validateRestrictions
argument_list|(
literal|"/test"
argument_list|,
name|aceTree
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
name|testValidateRestrictionsWrongType
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|aceTree
init|=
name|getAceTree
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|MANDATORY_BOOLEAN_RESTRICTION
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"string"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|provider
operator|.
name|validateRestrictions
argument_list|(
literal|"/test"
argument_list|,
name|aceTree
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
name|testValidateRestrictionsInvalidDefinition
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|rWithInvalidDefinition
init|=
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_GLOB
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"str"
argument_list|,
literal|"str2"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Tree
name|aceTree
init|=
name|getAceTree
argument_list|(
name|rWithInvalidDefinition
argument_list|,
name|MANDATORY_BOOLEAN_RESTRICTION
argument_list|)
decl_stmt|;
name|RestrictionProvider
name|rp
init|=
operator|new
name|AbstractRestrictionProvider
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|REP_GLOB
argument_list|,
name|GLOB_RESTRICTION
operator|.
name|getDefinition
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Restriction
argument_list|>
name|readRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|aceTree
parameter_list|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|rWithInvalidDefinition
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
name|RestrictionProvider
name|cp
init|=
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|rp
argument_list|,
name|rp2
argument_list|)
decl_stmt|;
name|cp
operator|.
name|validateRestrictions
argument_list|(
literal|"/test"
argument_list|,
name|aceTree
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
name|testValidateRestrictionsUnsupported
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|aceTree
init|=
name|getAceTree
argument_list|(
name|UNKNOWN_RESTRICTION
argument_list|,
name|MANDATORY_BOOLEAN_RESTRICTION
argument_list|)
decl_stmt|;
name|RestrictionProvider
name|rp
init|=
operator|new
name|AbstractRestrictionProvider
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|REP_GLOB
argument_list|,
name|GLOB_RESTRICTION
operator|.
name|getDefinition
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Restriction
argument_list|>
name|readRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|aceTree
parameter_list|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|UNKNOWN_RESTRICTION
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
name|RestrictionProvider
name|cp
init|=
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|rp
argument_list|,
name|rp2
argument_list|)
decl_stmt|;
name|cp
operator|.
name|validateRestrictions
argument_list|(
literal|"/test"
argument_list|,
name|aceTree
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRestrictionPatternEmptyComposite
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|()
operator|.
name|getPattern
argument_list|(
literal|"/test"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|GLOB_RESTRICTION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRestrictionPatternSingleEmpty
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
operator|new
name|AbstractRestrictionProvider
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
block|}
argument_list|)
operator|.
name|getPattern
argument_list|(
literal|"/test"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|GLOB_RESTRICTION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRestrictionPatternAllEmpty
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
operator|new
name|AbstractRestrictionProvider
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
block|}
argument_list|,
operator|new
name|AbstractRestrictionProvider
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
block|}
argument_list|)
operator|.
name|getPattern
argument_list|(
literal|"/test"
argument_list|,
name|getAceTree
argument_list|(
name|NT_PREFIXES_RESTRICTION
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

