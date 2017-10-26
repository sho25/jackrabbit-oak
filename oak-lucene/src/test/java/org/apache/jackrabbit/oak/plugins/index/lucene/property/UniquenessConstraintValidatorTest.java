begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|lucene
operator|.
name|property
package|;
end_package

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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|IndexDefinition
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
name|index
operator|.
name|lucene
operator|.
name|PropertyDefinition
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
name|index
operator|.
name|lucene
operator|.
name|PropertyUpdateCallback
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
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|IndexDefinitionBuilder
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
name|NodeState
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|InitialContent
operator|.
name|INITIAL_CONTENT
import|;
end_import

begin_import
import|import static
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
operator|.
name|CONSTRAINT
import|;
end_import

begin_import
import|import static
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
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_import
import|import static
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
operator|.
name|createProperty
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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
name|assertThat
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
name|UniquenessConstraintValidatorTest
block|{
specifier|private
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|IndexDefinitionBuilder
name|defnb
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
specifier|private
name|String
name|indexPath
init|=
literal|"/oak:index/foo"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|singleUniqueProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|PropertyUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|callback
operator|.
name|propertyUpdated
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|pd
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|callback
operator|.
name|propertyUpdated
argument_list|(
literal|"/b"
argument_list|,
literal|"foo"
argument_list|,
name|pd
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|,
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|CONSTRAINT
argument_list|,
name|e
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|indexPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|multipleUniqueProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo2"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|PropertyUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"foo2"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
comment|//As properties are different this should pass
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|firstStore_PreExist
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|PropertyUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|callback
operator|=
name|newCallback
argument_list|()
expr_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/b"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|secondStore_SamePath
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|PropertyIndexUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|callback
operator|.
name|getUniquenessConstraintValidator
argument_list|()
operator|.
name|setSecondStore
argument_list|(
parameter_list|(
name|propertyRelativePath
parameter_list|,
name|value
parameter_list|)
lambda|->
name|singletonList
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Should work as paths for unique property are same
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|secondStore_DiffPath
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|PropertyIndexUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|callback
operator|.
name|getUniquenessConstraintValidator
argument_list|()
operator|.
name|setSecondStore
argument_list|(
parameter_list|(
name|propertyRelativePath
parameter_list|,
name|value
parameter_list|)
lambda|->
name|singletonList
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|secondStore_NodeNotExist
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|PropertyIndexUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|callback
operator|.
name|getUniquenessConstraintValidator
argument_list|()
operator|.
name|setSecondStore
argument_list|(
parameter_list|(
name|propertyRelativePath
parameter_list|,
name|value
parameter_list|)
lambda|->
name|singletonList
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|secondStore_NodeExist_PropertyNotExist
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|root
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|PropertyIndexUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|callback
operator|.
name|getUniquenessConstraintValidator
argument_list|()
operator|.
name|setSecondStore
argument_list|(
parameter_list|(
name|propertyRelativePath
parameter_list|,
name|value
parameter_list|)
lambda|->
name|singletonList
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|secondStore_NodeExist_PropertyExist_DifferentValue
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|root
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|PropertyIndexUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|callback
operator|.
name|getUniquenessConstraintValidator
argument_list|()
operator|.
name|setSecondStore
argument_list|(
parameter_list|(
name|propertyRelativePath
parameter_list|,
name|value
parameter_list|)
lambda|->
name|singletonList
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|secondStore_RelativeProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"jcr:content/foo"
argument_list|)
operator|.
name|unique
argument_list|()
expr_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|PropertyIndexUpdateCallback
name|callback
init|=
name|newCallback
argument_list|()
decl_stmt|;
name|propertyUpdated
argument_list|(
name|callback
argument_list|,
literal|"/a"
argument_list|,
literal|"jcr:content/foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|callback
operator|.
name|getUniquenessConstraintValidator
argument_list|()
operator|.
name|setSecondStore
argument_list|(
parameter_list|(
name|propertyRelativePath
parameter_list|,
name|value
parameter_list|)
lambda|->
name|singletonList
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|callback
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|propertyUpdated
parameter_list|(
name|PropertyUpdateCallback
name|callback
parameter_list|,
name|String
name|nodePath
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|callback
operator|.
name|propertyUpdated
argument_list|(
name|nodePath
argument_list|,
name|propertyName
argument_list|,
name|pd
argument_list|(
name|propertyName
argument_list|)
argument_list|,
literal|null
argument_list|,
name|createProperty
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PropertyIndexUpdateCallback
name|newCallback
parameter_list|()
block|{
return|return
operator|new
name|PropertyIndexUpdateCallback
argument_list|(
name|indexPath
argument_list|,
name|builder
argument_list|,
name|root
argument_list|)
return|;
block|}
specifier|private
name|PropertyDefinition
name|pd
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defnb
operator|.
name|build
argument_list|()
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
return|return
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|getConfig
argument_list|(
name|propName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

