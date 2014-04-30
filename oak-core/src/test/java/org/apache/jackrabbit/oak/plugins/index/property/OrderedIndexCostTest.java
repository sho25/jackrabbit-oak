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
name|IndexUtils
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextExpression
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
name|query
operator|.
name|Filter
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
name|query
operator|.
name|PropertyValues
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
name|easymock
operator|.
name|EasyMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * tests the Cost-related part of the provider/strategy  */
end_comment

begin_class
specifier|public
class|class
name|OrderedIndexCostTest
extends|extends
name|BasicOrderedPropertyIndexQueryTest
block|{
comment|/**      * convenience class that return an always indexed strategy      */
specifier|private
specifier|static
class|class
name|AlwaysIndexedOrderedPropertyIndex
extends|extends
name|OrderedPropertyIndex
block|{
annotation|@
name|Override
name|AlwaysIndexedLookup
name|getLookup
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
return|return
operator|new
name|AlwaysIndexedLookup
argument_list|(
name|root
argument_list|)
return|;
block|}
comment|/**          * convenience class that always return true at the isIndexed test          */
specifier|private
specifier|static
class|class
name|AlwaysIndexedLookup
extends|extends
name|OrderedPropertyIndexLookup
block|{
specifier|public
name|AlwaysIndexedLookup
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isIndexed
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|path
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createTestIndexNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// intentionally left blank. Each test will have to define its own index configuration
block|}
specifier|private
specifier|static
name|void
name|defineIndex
parameter_list|(
name|NodeBuilder
name|root
parameter_list|,
name|OrderDirection
name|direction
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|root
operator|.
name|child
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
name|TEST_INDEX_NAME
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|ORDERED_PROPERTY
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OrderedIndex
operator|.
name|TYPE
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|direction
operator|.
name|getDirection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// forcing the existence of :index
name|root
operator|.
name|getChildNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|TEST_INDEX_NAME
argument_list|)
operator|.
name|child
argument_list|(
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
block|}
comment|/**      * define e descending ordered index in the provided root      *      * @param root      * @throws IllegalArgumentException      * @throws RepositoryException      */
specifier|private
specifier|static
name|void
name|defineDescendingIndex
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|defineIndex
argument_list|(
name|root
argument_list|,
name|OrderDirection
operator|.
name|DESC
argument_list|)
expr_stmt|;
block|}
comment|/**      * define e Ascending ordered index in the provided root      *      * @param root      * @throws IllegalArgumentException      * @throws RepositoryException      */
specifier|private
specifier|static
name|void
name|defineAscendingIndex
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|defineIndex
argument_list|(
name|root
argument_list|,
name|OrderDirection
operator|.
name|ASC
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costFullTextConstraint
parameter_list|()
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|OrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
decl_stmt|;
name|Filter
name|filter
init|=
name|EasyMock
operator|.
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|FullTextExpression
name|fte
init|=
name|EasyMock
operator|.
name|createNiceMock
argument_list|(
name|FullTextExpression
operator|.
name|class
argument_list|)
decl_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|fte
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|EasyMock
operator|.
name|replay
argument_list|(
name|fte
argument_list|)
expr_stmt|;
name|EasyMock
operator|.
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"if it contains FullText we don't serve"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costContainsNativeConstraints
parameter_list|()
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|OrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
decl_stmt|;
name|Filter
name|filter
init|=
name|EasyMock
operator|.
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|EasyMock
operator|.
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
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
name|EasyMock
operator|.
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If it contains Natives we don't serve"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * tests the use-case where we ask for '>' of a date.      *      * As we're not testing the actual algorithm, part of {@code IndexLookup} we want to make sure      * the Index doesn't reply with "dont' serve" in special cases      */
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costGreaterThanAscendingDirection
parameter_list|()
throws|throws
name|Exception
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineAscendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|first
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"In ascending order we're expeting to serve this kind of queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * test that the '>=' use case is served from the index      * @throws RepositoryException      * @throws IllegalArgumentException      */
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costGreaterThanEqualAscendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineAscendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|first
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|firstIncluding
operator|=
literal|true
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"In ascending order we're expeting to serve this kind of queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * when we run a '<' in an Ascending index it should not serve it      * @throws RepositoryException      * @throws IllegalArgumentException      */
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costLessThanAscendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineAscendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|last
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"in ascending index we're not expecting to serve '<' queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costLessThanEqualsAscendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineAscendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|last
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|lastIncluding
operator|=
literal|true
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"in ascending index we're not expecting to serve '<=' queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costGreaterThanDescendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineDescendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|first
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"in descending index we're not expecting to serve '>' queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costGreaterEqualThanDescendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineDescendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|first
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|firstIncluding
operator|=
literal|true
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"in descending index we're not expecting to serve '>' queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costLessThanDescendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineDescendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|last
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"In descending order we're expeting to serve this kind of queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costLessThanEqualDescendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineDescendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|last
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|lastIncluding
operator|=
literal|true
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"In descending order we're expeting to serve this kind of queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costBetweenDescendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineDescendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|first
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|last
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-02"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|firstIncluding
operator|=
literal|true
expr_stmt|;
name|restriction
operator|.
name|lastIncluding
operator|=
literal|true
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"In descending order we're expeting to serve this kind of queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"As of OAK-622 this should no longer be used. Removing later."
argument_list|)
specifier|public
name|void
name|costBetweenAscendingDirection
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|RepositoryException
block|{
name|OrderedPropertyIndex
name|index
init|=
operator|new
name|AlwaysIndexedOrderedPropertyIndex
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
decl_stmt|;
name|defineAscendingIndex
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|createNiceMock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
operator|new
name|Filter
operator|.
name|PropertyRestriction
argument_list|()
decl_stmt|;
name|restriction
operator|.
name|first
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-01"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|last
operator|=
name|PropertyValues
operator|.
name|newDate
argument_list|(
literal|"2013-01-02"
argument_list|)
expr_stmt|;
name|restriction
operator|.
name|firstIncluding
operator|=
literal|true
expr_stmt|;
name|restriction
operator|.
name|lastIncluding
operator|=
literal|true
expr_stmt|;
name|restriction
operator|.
name|propertyName
operator|=
name|ORDERED_PROPERTY
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|restriction
argument_list|)
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"In descending order we're expeting to serve this kind of queries"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
operator|==
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

