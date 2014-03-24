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
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
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
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

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
name|PropertyValue
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
name|ResultRow
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
name|Iterables
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
name|Lists
import|;
end_import

begin_class
specifier|public
class|class
name|OrderedPropertyIndexDescendingQueryTest
extends|extends
name|BasicOrderedPropertyIndexQueryTest
block|{
annotation|@
name|Override
specifier|protected
name|void
name|createTestIndexNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|index
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
operator|new
name|NodeUtil
argument_list|(
name|index
operator|.
name|getChild
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|)
argument_list|,
name|TEST_INDEX_NAME
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
name|ORDERED_PROPERTY
block|}
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
name|OrderedIndex
operator|.
name|OrderDirection
operator|.
name|DESC
operator|.
name|getDirection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|/**      * Query the index for retrieving all the entries      *      * @throws CommitFailedException      * @throws ParseException      * @throws RepositoryException      */
annotation|@
name|Test
specifier|public
name|void
name|queryAllEntries
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|ParseException
throws|,
name|RepositoryException
block|{
name|setTravesalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// index automatically created by the framework:
comment|// {@code createTestIndexNode()}
name|Tree
name|rTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|test
init|=
name|rTree
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ValuePathTuple
argument_list|>
name|nodes
init|=
name|addChildNodes
argument_list|(
name|generateOrderedValues
argument_list|(
name|NUMBER_OF_NODES
argument_list|,
name|OrderDirection
operator|.
name|DESC
argument_list|)
argument_list|,
name|test
argument_list|,
name|OrderDirection
operator|.
name|DESC
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// querying
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|results
decl_stmt|;
name|results
operator|=
name|executeQuery
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"SELECT * from [%s] WHERE foo IS NOT NULL"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
argument_list|,
name|SQL2
argument_list|,
literal|null
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertRightOrder
argument_list|(
name|nodes
argument_list|,
name|results
argument_list|)
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * test the index for returning the items related to a single key      *      * @throws CommitFailedException      * @throws ParseException      */
annotation|@
name|Test
specifier|public
name|void
name|queryOneKey
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|ParseException
block|{
name|setTravesalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// index automatically created by the framework:
comment|// {@code createTestIndexNode()}
name|Tree
name|rTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|test
init|=
name|rTree
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ValuePathTuple
argument_list|>
name|nodes
init|=
name|addChildNodes
argument_list|(
name|generateOrderedValues
argument_list|(
name|NUMBER_OF_NODES
argument_list|)
argument_list|,
name|test
argument_list|,
name|OrderDirection
operator|.
name|DESC
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// getting the middle of the random list of nodes
name|ValuePathTuple
name|searchfor
init|=
name|nodes
operator|.
name|get
argument_list|(
name|NUMBER_OF_NODES
operator|/
literal|2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|filter
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|ORDERED_PROPERTY
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|searchfor
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"SELECT * FROM [%s] WHERE %s=$%s"
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|results
init|=
name|executeQuery
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|query
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|ORDERED_PROPERTY
argument_list|,
name|ORDERED_PROPERTY
argument_list|)
argument_list|,
name|SQL2
argument_list|,
name|filter
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"one element is expected"
argument_list|,
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong path returned"
argument_list|,
name|searchfor
operator|.
name|getPath
argument_list|()
argument_list|,
name|results
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"there should be not any more items"
argument_list|,
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * test the range query in case of '>' condition      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|queryGreaterThan
parameter_list|()
throws|throws
name|Exception
block|{
name|initWithProperProvider
argument_list|()
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|OrderDirection
name|direction
init|=
name|OrderDirection
operator|.
name|ASC
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"SELECT * FROM [nt:base] AS n WHERE n.%s> $%s"
decl_stmt|;
comment|// index automatically created by the framework:
comment|// {@code createTestIndexNode()}
comment|// initialising the data
name|Tree
name|rTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|test
init|=
name|rTree
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Calendar
name|start
init|=
name|midnightFirstJan2013
argument_list|()
decl_stmt|;
name|addChildNodes
argument_list|(
name|generateOrderedDates
argument_list|(
name|NUMBER_OF_NODES
argument_list|,
name|direction
argument_list|,
name|start
argument_list|)
argument_list|,
name|test
argument_list|,
name|direction
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Calendar
name|searchForCalendar
init|=
operator|(
name|Calendar
operator|)
name|start
operator|.
name|clone
argument_list|()
decl_stmt|;
name|searchForCalendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|36
argument_list|)
expr_stmt|;
name|String
name|searchFor
init|=
name|ISO_8601_2000
operator|.
name|format
argument_list|(
name|searchForCalendar
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|filter
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|ORDERED_PROPERTY
argument_list|,
name|PropertyValues
operator|.
name|newDate
argument_list|(
name|searchFor
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|results
init|=
name|executeQuery
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|query
argument_list|,
name|ORDERED_PROPERTY
argument_list|,
name|ORDERED_PROPERTY
argument_list|)
argument_list|,
name|SQL2
argument_list|,
name|filter
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"We should not return any results as of the cost"
argument_list|,
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * test the range query in case of '>=' condition      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|queryGreaterEqualThan
parameter_list|()
throws|throws
name|Exception
block|{
name|initWithProperProvider
argument_list|()
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|OrderDirection
name|direction
init|=
name|OrderDirection
operator|.
name|ASC
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"SELECT * FROM [nt:base] AS n WHERE n.%s>= $%s"
decl_stmt|;
comment|// index automatically created by the framework:
comment|// {@code createTestIndexNode()}
comment|// initialising the data
name|Tree
name|rTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|test
init|=
name|rTree
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Calendar
name|start
init|=
name|midnightFirstJan2013
argument_list|()
decl_stmt|;
name|addChildNodes
argument_list|(
name|generateOrderedDates
argument_list|(
name|NUMBER_OF_NODES
argument_list|,
name|direction
argument_list|,
name|start
argument_list|)
argument_list|,
name|test
argument_list|,
name|direction
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Calendar
name|searchForCalendar
init|=
operator|(
name|Calendar
operator|)
name|start
operator|.
name|clone
argument_list|()
decl_stmt|;
name|searchForCalendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|36
argument_list|)
expr_stmt|;
name|String
name|searchFor
init|=
name|ISO_8601_2000
operator|.
name|format
argument_list|(
name|searchForCalendar
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|filter
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|ORDERED_PROPERTY
argument_list|,
name|PropertyValues
operator|.
name|newDate
argument_list|(
name|searchFor
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|results
init|=
name|executeQuery
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|query
argument_list|,
name|ORDERED_PROPERTY
argument_list|,
name|ORDERED_PROPERTY
argument_list|)
argument_list|,
name|SQL2
argument_list|,
name|filter
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"We should not return any results as of the cost"
argument_list|,
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * test the range query in case of '<' condition      *      * in this case as we're ascending we're expecting an empty resultset with the proper      * provider. not the lowcost one.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|queryLessThan
parameter_list|()
throws|throws
name|Exception
block|{
name|setTravesalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|OrderDirection
name|direction
init|=
name|OrderDirection
operator|.
name|DESC
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"SELECT * FROM [nt:base] AS n WHERE n.%s< $%s"
decl_stmt|;
comment|// index automatically created by the framework:
comment|// {@code createTestIndexNode()}
comment|// initialising the data
name|Tree
name|rTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|test
init|=
name|rTree
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Calendar
name|start
init|=
name|midnightFirstJan2013
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ValuePathTuple
argument_list|>
name|nodes
init|=
name|addChildNodes
argument_list|(
name|generateOrderedDates
argument_list|(
literal|10
argument_list|,
name|direction
argument_list|,
name|start
argument_list|)
argument_list|,
name|test
argument_list|,
name|direction
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Calendar
name|searchForCalendar
init|=
operator|(
name|Calendar
operator|)
name|start
operator|.
name|clone
argument_list|()
decl_stmt|;
name|searchForCalendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
operator|-
literal|36
argument_list|)
expr_stmt|;
name|String
name|searchFor
init|=
name|ISO_8601_2000
operator|.
name|format
argument_list|(
name|searchForCalendar
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|filter
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|ORDERED_PROPERTY
argument_list|,
name|PropertyValues
operator|.
name|newDate
argument_list|(
name|searchFor
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|results
init|=
name|executeQuery
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|query
argument_list|,
name|ORDERED_PROPERTY
argument_list|,
name|ORDERED_PROPERTY
argument_list|)
argument_list|,
name|SQL2
argument_list|,
name|filter
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ValuePathTuple
argument_list|>
name|filtered
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|nodes
argument_list|,
operator|new
name|ValuePathTuple
operator|.
name|LessThanPredicate
argument_list|(
name|searchFor
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertRightOrder
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|filtered
argument_list|)
argument_list|,
name|results
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"no more results expected"
argument_list|,
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * test the range query in case of '<=' condition      *      * in this case as we're ascending we're expecting an empty resultset with the proper      * provider. not the lowcost one.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|queryLessEqualThan
parameter_list|()
throws|throws
name|Exception
block|{
name|setTravesalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|OrderDirection
name|direction
init|=
name|OrderDirection
operator|.
name|DESC
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"SELECT * FROM [nt:base] AS n WHERE n.%s<= $%s"
decl_stmt|;
comment|// index automatically created by the framework:
comment|// {@code createTestIndexNode()}
comment|// initialising the data
name|Tree
name|rTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|test
init|=
name|rTree
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Calendar
name|start
init|=
name|midnightFirstJan2013
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ValuePathTuple
argument_list|>
name|nodes
init|=
name|addChildNodes
argument_list|(
name|generateOrderedDates
argument_list|(
literal|10
argument_list|,
name|direction
argument_list|,
name|start
argument_list|)
argument_list|,
name|test
argument_list|,
name|direction
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Calendar
name|searchForCalendar
init|=
operator|(
name|Calendar
operator|)
name|start
operator|.
name|clone
argument_list|()
decl_stmt|;
name|searchForCalendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
operator|-
literal|36
argument_list|)
expr_stmt|;
name|String
name|searchFor
init|=
name|ISO_8601_2000
operator|.
name|format
argument_list|(
name|searchForCalendar
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|filter
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|ORDERED_PROPERTY
argument_list|,
name|PropertyValues
operator|.
name|newDate
argument_list|(
name|searchFor
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|results
init|=
name|executeQuery
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|query
argument_list|,
name|ORDERED_PROPERTY
argument_list|,
name|ORDERED_PROPERTY
argument_list|)
argument_list|,
name|SQL2
argument_list|,
name|filter
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ValuePathTuple
argument_list|>
name|filtered
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|nodes
argument_list|,
operator|new
name|ValuePathTuple
operator|.
name|LessThanPredicate
argument_list|(
name|searchFor
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertRightOrder
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|filtered
argument_list|)
argument_list|,
name|results
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"no more results expecrted"
argument_list|,
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * testing explicitly OAK-1561 use-case      *       * @throws CommitFailedException      * @throws ParseException      */
annotation|@
name|Test
specifier|public
name|void
name|queryGreaterThenWithCast
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|ParseException
block|{
name|setTravesalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|OrderDirection
name|direction
init|=
name|OrderDirection
operator|.
name|ASC
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"SELECT * FROM [nt:base] WHERE "
operator|+
name|ORDERED_PROPERTY
operator|+
literal|"> cast('%s' as date)"
decl_stmt|;
comment|// index automatically created by the framework:
comment|// {@code createTestIndexNode()}
comment|// initialising the data
name|Tree
name|rTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|test
init|=
name|rTree
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Calendar
name|start
init|=
name|midnightFirstJan2013
argument_list|()
decl_stmt|;
name|addChildNodes
argument_list|(
name|generateOrderedDates
argument_list|(
name|NUMBER_OF_NODES
argument_list|,
name|direction
argument_list|,
name|start
argument_list|)
argument_list|,
name|test
argument_list|,
name|direction
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Calendar
name|searchForCalendar
init|=
operator|(
name|Calendar
operator|)
name|start
operator|.
name|clone
argument_list|()
decl_stmt|;
name|searchForCalendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|36
argument_list|)
expr_stmt|;
name|String
name|searchFor
init|=
name|ISO_8601_2000
operator|.
name|format
argument_list|(
name|searchForCalendar
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|results
init|=
name|executeQuery
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|query
argument_list|,
name|searchFor
argument_list|)
argument_list|,
name|SQL2
argument_list|,
literal|null
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"the index should not be used in this case"
argument_list|,
name|results
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|setTravesalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

