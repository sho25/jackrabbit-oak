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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|RestrictionImpl
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
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionProvider
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Set
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
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyString
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|never
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
name|times
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
name|verify
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
name|PrincipalRestrictionProviderTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|AccessControlConstants
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PrincipalRestrictionProviderTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|RestrictionProvider
name|base
init|=
name|mock
argument_list|(
name|RestrictionProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|PrincipalRestrictionProvider
name|provider
init|=
operator|new
name|PrincipalRestrictionProvider
argument_list|(
name|base
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetSupportedDefinitions
parameter_list|()
block|{
name|when
argument_list|(
name|base
operator|.
name|getSupportedRestrictions
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
name|emptySet
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
literal|1
argument_list|,
name|defs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|REP_NODE_PATH
argument_list|,
name|defs
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
name|mock
argument_list|(
name|Restriction
operator|.
name|class
argument_list|)
decl_stmt|;
name|Value
name|v
init|=
name|mock
argument_list|(
name|Value
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|base
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
literal|"name"
argument_list|,
name|v
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|r
argument_list|,
name|provider
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
literal|"name"
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|base
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
literal|"name"
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateNodePathRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|emptyV
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|Value
operator|.
name|class
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|""
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|Restriction
name|r
init|=
name|provider
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
name|REP_NODE_PATH
argument_list|,
name|emptyV
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r
operator|instanceof
name|RestrictionImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
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
name|Value
name|v
init|=
name|getValueFactory
argument_list|(
name|root
argument_list|)
operator|.
name|createValue
argument_list|(
literal|"/path"
argument_list|)
decl_stmt|;
name|r
operator|=
name|provider
operator|.
name|createRestriction
argument_list|(
literal|"/testPath"
argument_list|,
name|REP_NODE_PATH
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|instanceof
name|RestrictionImpl
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/path"
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
name|verify
argument_list|(
name|base
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|createRestriction
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|Value
operator|.
name|class
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
name|Tree
name|t
init|=
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|Value
name|v
init|=
name|getValueFactory
argument_list|(
name|root
argument_list|)
operator|.
name|createValue
argument_list|(
literal|"/path"
argument_list|)
decl_stmt|;
name|PropertyState
name|ps
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_NODE_PATH
argument_list|,
name|v
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Restriction
argument_list|>
name|rs
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|ps
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|provider
operator|.
name|writeRestrictions
argument_list|(
literal|"/testPath"
argument_list|,
name|t
argument_list|,
name|rs
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|base
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|writeRestrictions
argument_list|(
literal|"/testPath"
argument_list|,
name|t
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|rs
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|base
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|writeRestrictions
argument_list|(
literal|"/testPath"
argument_list|,
name|t
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|()
argument_list|)
expr_stmt|;
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
name|Tree
name|t
init|=
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|provider
operator|.
name|validateRestrictions
argument_list|(
literal|"/testPath"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|base
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|validateRestrictions
argument_list|(
literal|"/testPath"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPatternFromTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|base
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|t
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
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
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|base
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|t
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
name|Restriction
name|r
init|=
name|mock
argument_list|(
name|Restriction
operator|.
name|class
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|provider
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|restrictions
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|base
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getPattern
argument_list|(
literal|"/testPath"
argument_list|,
name|restrictions
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

