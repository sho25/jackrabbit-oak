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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|user
operator|.
name|Authorizable
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
name|User
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
name|commons
operator|.
name|PathUtils
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|principal
operator|.
name|PrincipalImpl
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

begin_class
specifier|public
class|class
name|AuthorizablePropertiesImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|AuthorizablePropertiesImpl
name|emptyProperties
decl_stmt|;
specifier|private
name|Authorizable
name|user2
decl_stmt|;
specifier|private
name|AuthorizablePropertiesImpl
name|properties
decl_stmt|;
specifier|private
name|ValueFactory
name|vf
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
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|emptyProperties
operator|=
operator|new
name|AuthorizablePropertiesImpl
argument_list|(
operator|(
name|AuthorizableImpl
operator|)
name|user
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|id2
init|=
literal|"user2"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|user2
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|id2
argument_list|,
literal|null
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
name|id2
argument_list|)
argument_list|,
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|vf
operator|=
name|getValueFactory
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|Value
name|v
init|=
name|vf
operator|.
name|createValue
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|Value
index|[]
name|vArr
init|=
operator|new
name|Value
index|[]
block|{
name|vf
operator|.
name|createValue
argument_list|(
literal|2
argument_list|)
block|,
name|vf
operator|.
name|createValue
argument_list|(
literal|30
argument_list|)
block|}
decl_stmt|;
name|user2
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|user2
operator|.
name|setProperty
argument_list|(
literal|"mvProp"
argument_list|,
name|vArr
argument_list|)
expr_stmt|;
name|user2
operator|.
name|setProperty
argument_list|(
literal|"relPath/prop"
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|user2
operator|.
name|setProperty
argument_list|(
literal|"relPath/mvProp"
argument_list|,
name|vArr
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|properties
operator|=
operator|new
name|AuthorizablePropertiesImpl
argument_list|(
operator|(
name|AuthorizableImpl
operator|)
name|user2
argument_list|,
name|getNamePathMapper
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
name|user2
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
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
comment|//-----------------------------------------------------------< getNames>---
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetNamesNullPath
parameter_list|()
throws|throws
name|Exception
block|{
name|emptyProperties
operator|.
name|getNames
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetNamesEmptyPath
parameter_list|()
throws|throws
name|Exception
block|{
name|emptyProperties
operator|.
name|getNames
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetNamesAbsPath
parameter_list|()
throws|throws
name|Exception
block|{
name|emptyProperties
operator|.
name|getNames
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetNamesOutSideScope
parameter_list|()
throws|throws
name|Exception
block|{
name|emptyProperties
operator|.
name|getNames
argument_list|(
literal|"../.."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetNamesOtherUser
parameter_list|()
throws|throws
name|Exception
block|{
name|emptyProperties
operator|.
name|getNames
argument_list|(
literal|"../"
operator|+
name|PathUtils
operator|.
name|getName
argument_list|(
name|user2
operator|.
name|getPath
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
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetNamesMissingResolutionToOakPath
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthorizableProperties
name|props
init|=
operator|new
name|AuthorizablePropertiesImpl
argument_list|(
operator|(
name|AuthorizableImpl
operator|)
name|user2
argument_list|,
operator|new
name|NamePathMapper
operator|.
name|Default
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getOakNameOrNull
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|props
operator|.
name|getNames
argument_list|(
literal|"relPath"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNamesCurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|emptyProperties
operator|.
name|getNames
argument_list|(
literal|"."
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNamesCurrent2
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|names
init|=
name|properties
operator|.
name|getNames
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"prop"
argument_list|,
literal|"mvProp"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|names
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetNamesNonExistingRelPath
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|getNames
argument_list|(
literal|"nonExisting"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNamesRelPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|names
init|=
name|properties
operator|.
name|getNames
argument_list|(
literal|"relPath"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"prop"
argument_list|,
literal|"mvProp"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|names
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------< setProperty>---
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetAuthorizableId
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"otherId"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetPrimaryType
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"relPath/"
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetNewProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"prop2"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|hasProperty
argument_list|(
literal|"prop2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetNewPropertyWithRelPath
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"relPath/prop2"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"val"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|hasProperty
argument_list|(
literal|"relPath/prop2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetNewPropertyNewRelPath
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"newPath/prop2"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"val"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|hasProperty
argument_list|(
literal|"newPath/prop2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetModifyProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|v
init|=
name|vf
operator|.
name|createValue
argument_list|(
literal|"newValue"
argument_list|)
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Value
index|[]
block|{
name|v
block|}
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPropertyChangeMvStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|v
init|=
name|vf
operator|.
name|createValue
argument_list|(
literal|"newValue"
argument_list|)
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"mvProp"
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Value
index|[]
block|{
name|v
block|}
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"mvProp"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPropertyChangeMvStatus2
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|v
init|=
name|vf
operator|.
name|createValue
argument_list|(
literal|"newValue"
argument_list|)
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"relPath/prop"
argument_list|,
operator|new
name|Value
index|[]
block|{
name|v
block|,
name|v
block|}
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Value
index|[]
block|{
name|v
block|,
name|v
block|}
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"relPath/prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetMissingResolutionToOakPath
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthorizableProperties
name|props
init|=
operator|new
name|AuthorizablePropertiesImpl
argument_list|(
operator|(
name|AuthorizableImpl
operator|)
name|user2
argument_list|,
operator|new
name|NamePathMapper
operator|.
name|Default
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getOakNameOrNull
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"relPath/prop"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPropertyNullArray
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"mvProp"
argument_list|,
operator|(
name|Value
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|hasProperty
argument_list|(
literal|"mvProp"
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"relPath/prop"
argument_list|,
operator|(
name|Value
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|hasProperty
argument_list|(
literal|"relPath/prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPropertyNull
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"mvProp"
argument_list|,
operator|(
name|Value
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|hasProperty
argument_list|(
literal|"mvProp"
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"relPath/prop"
argument_list|,
operator|(
name|Value
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|hasProperty
argument_list|(
literal|"relPath/prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetOutSideScope
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"../../prop"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"newValue"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetPropertyOtherUser
parameter_list|()
throws|throws
name|Exception
block|{
name|emptyProperties
operator|.
name|setProperty
argument_list|(
literal|"../"
operator|+
name|PathUtils
operator|.
name|getName
argument_list|(
name|user2
operator|.
name|getPath
argument_list|()
argument_list|)
operator|+
literal|"/prop"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"newValue"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------------------------------< removeProperty>---
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemoveAuthorizableId
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|removeProperty
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemovePrimaryType
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|removeProperty
argument_list|(
literal|"relPath/"
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveNonExisting
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|properties
operator|.
name|removeProperty
argument_list|(
literal|"nonExisting"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|removeProperty
argument_list|(
literal|"relPath/nonExisting"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|emptyProperties
operator|.
name|removeProperty
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|properties
operator|.
name|removeProperty
argument_list|(
literal|"mvProp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|removeProperty
argument_list|(
literal|"relPath/prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemoveMissingResolutionToOakPath
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthorizableProperties
name|props
init|=
operator|new
name|AuthorizablePropertiesImpl
argument_list|(
operator|(
name|AuthorizableImpl
operator|)
name|user2
argument_list|,
operator|new
name|NamePathMapper
operator|.
name|Default
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getOakNameOrNull
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|props
operator|.
name|removeProperty
argument_list|(
literal|"relPath/prop"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemovePropertyOutSideScope
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|removeProperty
argument_list|(
literal|"../../"
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemoveNonExistingPropertyOutSideScope
parameter_list|()
throws|throws
name|Exception
block|{
name|properties
operator|.
name|removeProperty
argument_list|(
literal|"../../nonExistingTree/nonExistingProp"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RepositoryException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemovePropertyOtherUser
parameter_list|()
throws|throws
name|Exception
block|{
name|emptyProperties
operator|.
name|removeProperty
argument_list|(
literal|"../"
operator|+
name|PathUtils
operator|.
name|getName
argument_list|(
name|user2
operator|.
name|getPath
argument_list|()
argument_list|)
operator|+
literal|"/prop"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

