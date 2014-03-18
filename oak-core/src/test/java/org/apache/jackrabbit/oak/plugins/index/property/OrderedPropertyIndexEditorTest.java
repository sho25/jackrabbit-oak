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
name|index
operator|.
name|property
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|createNiceMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|index
operator|.
name|IndexConstants
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
name|property
operator|.
name|OrderedIndex
operator|.
name|OrderDirection
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
name|EmptyNodeState
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests the ordered index.  */
end_comment

begin_class
specifier|public
class|class
name|OrderedPropertyIndexEditorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|isProperlyConfiguredWithPropertyNames
parameter_list|()
block|{
name|NodeBuilder
name|definition
init|=
name|createNiceMock
argument_list|(
name|NodeBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
name|PropertyState
name|names
init|=
name|createNiceMock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|names
operator|.
name|count
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|definition
operator|.
name|getProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|names
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|definition
argument_list|)
expr_stmt|;
name|OrderedPropertyIndexEditor
name|ie
init|=
operator|new
name|OrderedPropertyIndexEditor
argument_list|(
name|definition
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"With empty or missing property the index should not work."
argument_list|,
name|ie
operator|.
name|isProperlyConfigured
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isProperlyConfiguredSingleValuePropertyNames
parameter_list|()
block|{
name|NodeBuilder
name|definition
init|=
name|createNiceMock
argument_list|(
name|NodeBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
name|PropertyState
name|names
init|=
name|createNiceMock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|names
operator|.
name|count
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|names
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"jcr:lastModified"
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|definition
operator|.
name|getProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|names
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|definition
argument_list|)
expr_stmt|;
name|OrderedPropertyIndexEditor
name|ie
init|=
operator|new
name|OrderedPropertyIndexEditor
argument_list|(
name|definition
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"With a correct property set 'propertyNames' can't be null"
argument_list|,
name|ie
operator|.
name|getPropertyNames
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ie
operator|.
name|getPropertyNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"jcr:lastModified"
argument_list|,
name|ie
operator|.
name|getPropertyNames
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expecting a properly configured index"
argument_list|,
name|ie
operator|.
name|isProperlyConfigured
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multiValueProperty
parameter_list|()
block|{
name|NodeBuilder
name|definition
init|=
name|createNiceMock
argument_list|(
name|NodeBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
name|PropertyState
name|names
init|=
name|createNiceMock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|names
operator|.
name|isArray
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|names
operator|.
name|count
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|2
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|names
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"jcr:lastModified"
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|names
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"foo:bar"
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|names
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"jcr:lastModified"
argument_list|,
literal|"foo:bar"
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|definition
operator|.
name|getProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|names
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|definition
argument_list|)
expr_stmt|;
name|OrderedPropertyIndexEditor
name|ie
init|=
operator|new
name|OrderedPropertyIndexEditor
argument_list|(
name|definition
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"With a correct property set 'propertyNames' can't be null"
argument_list|,
name|ie
operator|.
name|getPropertyNames
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"When multiple properties are a passed only the first one is taken"
argument_list|,
literal|1
argument_list|,
name|ie
operator|.
name|getPropertyNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"jcr:lastModified"
argument_list|,
name|ie
operator|.
name|getPropertyNames
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expecting a properly configured index"
argument_list|,
name|ie
operator|.
name|isProperlyConfigured
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orderDirectionDefinitionNotSpecified
parameter_list|()
block|{
specifier|final
name|String
name|property
init|=
literal|"foobar"
decl_stmt|;
name|NodeBuilder
name|definition
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|OrderedPropertyIndexEditor
name|editor
init|=
operator|new
name|OrderedPropertyIndexEditor
argument_list|(
name|definition
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|editor
operator|.
name|getPropertyNames
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|property
argument_list|,
name|editor
operator|.
name|getPropertyNames
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrderedIndex
operator|.
name|OrderDirection
operator|.
name|ASC
argument_list|,
name|editor
operator|.
name|getDirection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orderDirectionDefinitionDescending
parameter_list|()
block|{
specifier|final
name|String
name|property
init|=
literal|"foobar"
decl_stmt|;
name|NodeBuilder
name|definition
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
literal|"descending"
argument_list|)
expr_stmt|;
name|OrderedPropertyIndexEditor
name|editor
init|=
operator|new
name|OrderedPropertyIndexEditor
argument_list|(
name|definition
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|editor
operator|.
name|getPropertyNames
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|property
argument_list|,
name|editor
operator|.
name|getPropertyNames
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrderedIndex
operator|.
name|OrderDirection
operator|.
name|DESC
argument_list|,
name|editor
operator|.
name|getDirection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orderDirectionUnknownDefinition
parameter_list|()
block|{
specifier|final
name|String
name|property
init|=
literal|"foobar"
decl_stmt|;
name|NodeBuilder
name|definition
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
literal|"bazbaz"
argument_list|)
expr_stmt|;
name|OrderedPropertyIndexEditor
name|editor
init|=
operator|new
name|OrderedPropertyIndexEditor
argument_list|(
name|definition
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|editor
operator|.
name|getPropertyNames
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|property
argument_list|,
name|editor
operator|.
name|getPropertyNames
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"if we provide a non-valid definition for order the Ascending is expected"
argument_list|,
name|OrderedIndex
operator|.
name|OrderDirection
operator|.
name|ASC
argument_list|,
name|editor
operator|.
name|getDirection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|strategies
parameter_list|()
block|{
specifier|final
name|String
name|property
init|=
literal|"foobar"
decl_stmt|;
name|NodeBuilder
name|definition
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|OrderDirection
operator|.
name|ASC
operator|.
name|getDirection
argument_list|()
argument_list|)
expr_stmt|;
name|OrderedPropertyIndexEditor
name|editor
init|=
operator|new
name|OrderedPropertyIndexEditor
argument_list|(
name|definition
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|OrderedPropertyIndexEditor
operator|.
name|ORDERED_MIRROR
argument_list|,
name|editor
operator|.
name|getStrategy
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|definition
operator|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
expr_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|OrderDirection
operator|.
name|DESC
operator|.
name|getDirection
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|=
operator|new
name|OrderedPropertyIndexEditor
argument_list|(
name|definition
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrderedPropertyIndexEditor
operator|.
name|ORDERED_MIRROR_DESCENDING
argument_list|,
name|editor
operator|.
name|getStrategy
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

