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
name|PropertyType
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
name|value
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|util
operator|.
name|NodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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

begin_class
specifier|public
class|class
name|AbstractRestrictionProviderTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|AccessControlConstants
block|{
specifier|private
name|String
name|unsupportedPath
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|testPath
init|=
literal|"/testRoot"
decl_stmt|;
specifier|private
name|Value
name|globValue
decl_stmt|;
specifier|private
name|Value
index|[]
name|nameValues
decl_stmt|;
specifier|private
name|Value
name|nameValue
decl_stmt|;
specifier|private
name|ValueFactory
name|valueFactory
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|RestrictionDefinition
argument_list|>
name|supported
decl_stmt|;
specifier|private
name|AbstractRestrictionProvider
name|restrictionProvider
decl_stmt|;
annotation|@
name|Before
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
name|valueFactory
operator|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
name|globValue
operator|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|nameValue
operator|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"nt:file"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|nameValues
operator|=
operator|new
name|Value
index|[]
block|{
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"nt:folder"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
block|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"nt:file"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
block|}
expr_stmt|;
name|RestrictionDefinition
name|glob
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|REP_GLOB
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|RestrictionDefinition
name|nts
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|RestrictionDefinition
name|mand
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
literal|"mandatory"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|supported
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|glob
operator|.
name|getName
argument_list|()
argument_list|,
name|glob
argument_list|,
name|nts
operator|.
name|getName
argument_list|()
argument_list|,
name|nts
argument_list|,
name|mand
operator|.
name|getName
argument_list|()
argument_list|,
name|mand
argument_list|)
expr_stmt|;
name|restrictionProvider
operator|=
operator|new
name|TestProvider
argument_list|(
name|supported
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
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
specifier|private
name|Tree
name|getAceTree
parameter_list|(
name|Restriction
modifier|...
name|restrictions
parameter_list|)
throws|throws
name|Exception
block|{
name|NodeUtil
name|rootNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|tmp
init|=
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"testRoot"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Tree
name|ace
init|=
name|tmp
operator|.
name|addChild
argument_list|(
literal|"rep:policy"
argument_list|,
name|NT_REP_ACL
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"ace0"
argument_list|,
name|NT_REP_GRANT_ACE
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|restrictionProvider
operator|.
name|writeRestrictions
argument_list|(
name|tmp
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|ace
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|restrictions
argument_list|)
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
name|testGetSupportedRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|RestrictionDefinition
argument_list|>
name|defs
init|=
name|restrictionProvider
operator|.
name|getSupportedRestrictions
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|defs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|supported
operator|.
name|size
argument_list|()
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
name|supported
operator|.
name|values
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|defs
operator|.
name|contains
argument_list|(
name|def
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetSupportedRestrictionsForUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|RestrictionDefinition
argument_list|>
name|defs
init|=
name|restrictionProvider
operator|.
name|getSupportedRestrictions
argument_list|(
name|unsupportedPath
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|defs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|defs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateForUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|unsupportedPath
argument_list|,
name|REP_GLOB
argument_list|,
name|globValue
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
try|try
block|{
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|unsupportedPath
argument_list|,
name|REP_NT_NAMES
argument_list|,
name|nameValues
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
name|testCreateForUnsupportedName
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|unsupportedPath
argument_list|,
literal|"unsupported"
argument_list|,
name|globValue
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
try|try
block|{
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|unsupportedPath
argument_list|,
literal|"unsupported"
argument_list|,
name|nameValues
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
name|testCreateForUnsupportedType
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|unsupportedPath
argument_list|,
name|REP_GLOB
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
try|try
block|{
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|unsupportedPath
argument_list|,
name|REP_NT_NAMES
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"nt:file"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
name|testCreateForUnsupportedMultiValues
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|unsupportedPath
argument_list|,
name|REP_GLOB
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"*"
argument_list|)
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"/a/*"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
name|testCreateRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|r
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_GLOB
argument_list|,
name|globValue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REP_GLOB
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|globValue
operator|.
name|getString
argument_list|()
argument_list|,
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateMvRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|r
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_NT_NAMES
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"nt:folder"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"nt:file"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|r
operator|.
name|getProperty
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ps
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|ps
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Value
argument_list|>
name|vs
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|ps
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|nameValues
argument_list|,
name|vs
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|vs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateMvRestriction2
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|r
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_NT_NAMES
argument_list|,
name|nameValues
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|r
operator|.
name|getProperty
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ps
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|ps
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Value
argument_list|>
name|vs
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|ps
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|nameValues
argument_list|,
name|vs
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|vs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateMvRestriction3
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|r
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_NT_NAMES
argument_list|,
name|nameValue
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Value
argument_list|>
name|vs
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Value
index|[]
block|{
name|nameValue
block|}
argument_list|,
name|vs
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|vs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateEmptyMvRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|r
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_NT_NAMES
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Value
argument_list|>
name|vs
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|vs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateEmptyMvRestriction2
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|r
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_NT_NAMES
argument_list|,
operator|new
name|Value
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Value
argument_list|>
name|vs
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|vs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadRestrictionsForUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
init|=
name|restrictionProvider
operator|.
name|readRestrictions
argument_list|(
name|unsupportedPath
argument_list|,
name|getAceTree
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|restrictions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|r
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_GLOB
argument_list|,
name|globValue
argument_list|)
decl_stmt|;
name|Tree
name|aceTree
init|=
name|getAceTree
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
init|=
name|restrictionProvider
operator|.
name|readRestrictions
argument_list|(
name|testPath
argument_list|,
name|aceTree
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|restrictions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|restrictions
operator|.
name|contains
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|r
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_GLOB
argument_list|,
name|globValue
argument_list|)
decl_stmt|;
name|Tree
name|aceTree
init|=
name|getAceTree
argument_list|()
decl_stmt|;
name|restrictionProvider
operator|.
name|writeRestrictions
argument_list|(
name|testPath
argument_list|,
name|aceTree
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Restriction
operator|>
name|of
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aceTree
operator|.
name|hasChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|restr
init|=
name|aceTree
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
argument_list|,
name|restr
operator|.
name|getProperty
argument_list|(
name|REP_GLOB
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteInvalidRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|PropertyState
name|ps
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_GLOB
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|aceTree
init|=
name|getAceTree
argument_list|()
decl_stmt|;
name|restrictionProvider
operator|.
name|writeRestrictions
argument_list|(
name|testPath
argument_list|,
name|aceTree
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|Restriction
operator|>
name|of
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|ps
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aceTree
operator|.
name|hasChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|restr
init|=
name|aceTree
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ps
argument_list|,
name|restr
operator|.
name|getProperty
argument_list|(
name|REP_GLOB
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidateRestrictionsUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
comment|// empty restrictions => must succeed
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
literal|null
argument_list|,
name|getAceTree
argument_list|()
argument_list|)
expr_stmt|;
comment|// non-empty restrictions => must fail
try|try
block|{
name|Restriction
name|restr
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_GLOB
argument_list|,
name|globValue
argument_list|)
decl_stmt|;
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
literal|null
argument_list|,
name|getAceTree
argument_list|(
name|restr
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
name|testValidateRestrictionsWrongType
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|mand
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
literal|"mandatory"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Tree
name|ace
init|=
name|getAceTree
argument_list|(
name|mand
argument_list|)
decl_stmt|;
operator|new
name|NodeUtil
argument_list|(
name|ace
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
operator|.
name|setBoolean
argument_list|(
name|REP_GLOB
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
name|testPath
argument_list|,
name|ace
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"wrong type with restriction 'rep:glob"
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
name|testValidateRestrictionsUnsupportedRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|mand
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
literal|"mandatory"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Tree
name|ace
init|=
name|getAceTree
argument_list|(
name|mand
argument_list|)
decl_stmt|;
operator|new
name|NodeUtil
argument_list|(
name|ace
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
operator|.
name|setString
argument_list|(
literal|"Unsupported"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
name|testPath
argument_list|,
name|ace
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"wrong type with restriction 'rep:glob"
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
name|testValidateRestrictionsMissingMandatory
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|glob
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_GLOB
argument_list|,
name|globValue
argument_list|)
decl_stmt|;
try|try
block|{
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
name|testPath
argument_list|,
name|getAceTree
argument_list|(
name|glob
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"missing mandatory restriction"
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
name|testValidateRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|Restriction
name|glob
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_GLOB
argument_list|,
name|globValue
argument_list|)
decl_stmt|;
name|Restriction
name|ntNames
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
name|REP_NT_NAMES
argument_list|,
name|nameValues
argument_list|)
decl_stmt|;
name|Restriction
name|mand
init|=
name|restrictionProvider
operator|.
name|createRestriction
argument_list|(
name|testPath
argument_list|,
literal|"mandatory"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
name|testPath
argument_list|,
name|getAceTree
argument_list|(
name|mand
argument_list|)
argument_list|)
expr_stmt|;
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
name|testPath
argument_list|,
name|getAceTree
argument_list|(
name|mand
argument_list|,
name|glob
argument_list|)
argument_list|)
expr_stmt|;
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
name|testPath
argument_list|,
name|getAceTree
argument_list|(
name|mand
argument_list|,
name|ntNames
argument_list|)
argument_list|)
expr_stmt|;
name|restrictionProvider
operator|.
name|validateRestrictions
argument_list|(
name|testPath
argument_list|,
name|getAceTree
argument_list|(
name|mand
argument_list|,
name|glob
argument_list|,
name|ntNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

