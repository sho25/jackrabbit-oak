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
name|jcr
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|ImmutableList
operator|.
name|of
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
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Calendar
operator|.
name|DAY_OF_MONTH
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Calendar
operator|.
name|HOUR_OF_DAY
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
name|reverseOrder
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
name|sort
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|DATE
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|NAME
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|STRING
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
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
name|index
operator|.
name|IndexConstants
operator|.
name|PROPERTY_NAMES
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
name|index
operator|.
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|index
operator|.
name|property
operator|.
name|OrderedIndex
operator|.
name|DIRECTION
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
name|index
operator|.
name|property
operator|.
name|OrderedIndex
operator|.
name|TYPE
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
name|index
operator|.
name|property
operator|.
name|OrderedIndex
operator|.
name|OrderDirection
operator|.
name|DESC
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|TimeZone
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
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Row
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|RowIterator
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
name|jcr
operator|.
name|util
operator|.
name|ValuePathTuple
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
name|test
operator|.
name|RepositoryStub
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
name|test
operator|.
name|RepositoryStubException
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
name|util
operator|.
name|ISO8601
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

begin_class
specifier|public
class|class
name|OrderedIndexIT
block|{
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_DEF_NODE
init|=
literal|"indexdef"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ORDERED_PROPERTY
init|=
literal|"foo"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT
init|=
literal|"content"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NODE_TYPE
init|=
name|NT_UNSTRUCTURED
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OrderedIndexIT
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|TimeZone
argument_list|>
name|TZS
init|=
name|of
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+01:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+02:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+03:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+05:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT-02:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT-04:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT-05:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT-07:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT-08:00"
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**       * define the index      *       * @param session      * @throws RepositoryException      */
specifier|private
name|void
name|createIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|Node
name|oakIndex
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|Node
name|indexDef
init|=
name|oakIndex
operator|.
name|addNode
argument_list|(
name|INDEX_DEF_NODE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|TYPE
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|PROPERTY_NAMES
argument_list|,
operator|new
name|String
index|[]
block|{
name|ORDERED_PROPERTY
block|}
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|DIRECTION
argument_list|,
name|DESC
operator|.
name|getDirection
argument_list|()
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|/**      * add a bunch of sequential nodes with the provided property values pick-up randomly and start      * numerating nodes from {@code startFrom}.      *       * The caller will have to perform the {@link Session#save()} after the method call.      *       * @param values the property values to set      * @param father under whom we should add the nodes      * @param propertyType the type of the property to be stored      * @return      * @throws RepositoryException       * @throws LockException       * @throws ConstraintViolationException       * @throws VersionException       * @throws PathNotFoundException       * @throws ItemExistsException       */
specifier|private
name|List
argument_list|<
name|ValuePathTuple
argument_list|>
name|addNodes
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Node
name|father
parameter_list|,
specifier|final
name|int
name|propertyType
parameter_list|,
specifier|final
name|int
name|startFrom
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|father
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|startFrom
operator|>=
literal|0
argument_list|,
literal|"startFrom must be>= 0"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|working
init|=
name|newArrayList
argument_list|(
name|checkNotNull
argument_list|(
name|values
argument_list|)
argument_list|)
decl_stmt|;
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
name|startFrom
decl_stmt|;
name|Node
name|n
decl_stmt|;
name|List
argument_list|<
name|ValuePathTuple
argument_list|>
name|vpt
init|=
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|working
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|v
init|=
name|working
operator|.
name|remove
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|working
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|=
name|father
operator|.
name|addNode
argument_list|(
literal|"n"
operator|+
name|counter
operator|++
argument_list|,
name|NODE_TYPE
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
name|ORDERED_PROPERTY
argument_list|,
name|v
argument_list|,
name|propertyType
argument_list|)
expr_stmt|;
name|vpt
operator|.
name|add
argument_list|(
operator|new
name|ValuePathTuple
argument_list|(
name|v
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|vpt
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|oak2035
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|RepositoryStubException
block|{
specifier|final
name|int
name|numberOfNodes
init|=
literal|1500
decl_stmt|;
specifier|final
name|String
name|statement
init|=
name|String
operator|.
name|format
argument_list|(
literal|"/jcr:root/content//element(*, %s) order by @%s descending"
argument_list|,
name|NODE_TYPE
argument_list|,
name|ORDERED_PROPERTY
argument_list|)
decl_stmt|;
name|Properties
name|env
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|env
operator|.
name|load
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/repositoryStubImpl.properties"
argument_list|)
argument_list|)
expr_stmt|;
name|RepositoryStub
name|stub
init|=
name|RepositoryStub
operator|.
name|getInstance
argument_list|(
name|env
argument_list|)
decl_stmt|;
name|Repository
name|repo
init|=
name|stub
operator|.
name|getRepository
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
name|Node
name|root
decl_stmt|,
name|content
decl_stmt|;
try|try
block|{
name|session
operator|=
name|repo
operator|.
name|login
argument_list|(
name|stub
operator|.
name|getSuperuserCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|createIndexDefinition
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|root
operator|.
name|hasNode
argument_list|(
name|CONTENT
argument_list|)
condition|)
block|{
name|root
operator|.
name|addNode
argument_list|(
name|CONTENT
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|content
operator|=
name|root
operator|.
name|getNode
argument_list|(
name|CONTENT
argument_list|)
expr_stmt|;
name|Calendar
name|start
init|=
name|midnightFirstJan2013
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dates
init|=
name|generateOrderedDates
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|numberOfNodes
argument_list|,
name|DESC
argument_list|,
name|start
argument_list|,
name|DAY_OF_MONTH
argument_list|,
literal|1
argument_list|,
name|TZS
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ValuePathTuple
argument_list|>
name|nodes
init|=
name|addNodes
argument_list|(
name|dates
argument_list|,
name|content
argument_list|,
name|DATE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// ensuring the correct order for checks
name|sort
argument_list|(
name|nodes
argument_list|,
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
for|for
control|(
name|ValuePathTuple
name|node
range|:
name|nodes
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|QueryManager
name|qm
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
name|qm
operator|.
name|createQuery
argument_list|(
name|statement
argument_list|,
name|Query
operator|.
name|XPATH
argument_list|)
decl_stmt|;
name|QueryResult
name|result
init|=
name|query
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertRightOrder
argument_list|(
name|nodes
argument_list|,
name|result
operator|.
name|getRows
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|assertRightOrder
parameter_list|(
specifier|final
name|List
argument_list|<
name|ValuePathTuple
argument_list|>
name|expected
parameter_list|,
specifier|final
name|RowIterator
name|obtained
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|obtained
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"the obtained result is empty"
argument_list|,
name|obtained
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ValuePathTuple
argument_list|>
name|exp
init|=
name|expected
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|exp
operator|.
name|hasNext
argument_list|()
operator|&&
name|obtained
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ValuePathTuple
name|vpt
init|=
name|exp
operator|.
name|next
argument_list|()
decl_stmt|;
name|Row
name|row
init|=
name|obtained
operator|.
name|nextRow
argument_list|()
decl_stmt|;
comment|// check manually about paths and dates
comment|// if paths don't match maybe the date does. It's the date we care, in case of multiple
comment|// paths under the same date the order of them is non-deterministic dependent on
comment|// persistence rules
if|if
condition|(
operator|!
name|vpt
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|row
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|property
init|=
name|row
operator|.
name|getNode
argument_list|()
operator|.
name|getProperty
argument_list|(
name|ORDERED_PROPERTY
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|vpt
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|property
argument_list|)
condition|)
block|{
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"both path and date failed to match. Expected: %s - %s. Obtained: %s, %s"
argument_list|,
name|vpt
operator|.
name|getPath
argument_list|()
argument_list|,
name|vpt
operator|.
name|getValue
argument_list|()
argument_list|,
name|row
operator|.
name|getPath
argument_list|()
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertFalse
argument_list|(
literal|"we should have processed all the expected"
argument_list|,
name|exp
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"we should have processed all the obtained"
argument_list|,
name|obtained
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// -------------------------------------< copied over from BasicOrderedPropertyIndexQueryTest>
comment|// TODO should we have anything in commons?
comment|/**      * generates a list of sorted dates as ISO8601 formatted strings.      *       * @param returnType Allowed values: {@link String} and {@link Long}. When String is specified      *            it will return ISO8601 formatted dates, otherwise the milliseconds.      * @param amount the amount of dates to be generated      * @param direction the direction of the sorting for the dates      * @param start the dates from where to start generating.      * @param increaseOf the {@link Calendar} field to increase while generating      * @param increaseBy the amount of increase to be used.      * @param timezones available timezones to be used in a random manner      * @param generateDuplicates if true it will generate a duplicate with a probability of 10%      * @return      */
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|generateOrderedDates
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|returnType
parameter_list|,
name|int
name|amount
parameter_list|,
annotation|@
name|Nonnull
name|OrderDirection
name|direction
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Calendar
name|start
parameter_list|,
name|int
name|increaseOf
parameter_list|,
name|int
name|increaseBy
parameter_list|,
annotation|@
name|Nonnull
name|List
argument_list|<
name|TimeZone
argument_list|>
name|timezones
parameter_list|,
name|boolean
name|generateDuplicates
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|allowedIncrease
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|HOUR_OF_DAY
argument_list|,
name|DAY_OF_MONTH
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|amount
operator|>
literal|0
argument_list|,
literal|"the amount must be> 0"
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|direction
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|allowedIncrease
operator|.
name|contains
argument_list|(
name|increaseOf
argument_list|)
argument_list|,
literal|"Wrong increaseOf. Allowed values: "
operator|+
name|allowedIncrease
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|increaseBy
operator|>
literal|0
argument_list|,
literal|"increaseBy must be a positive number"
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|timezones
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|returnType
operator|.
name|equals
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|||
name|returnType
operator|.
name|equals
argument_list|(
name|Long
operator|.
name|class
argument_list|)
argument_list|,
literal|"only String and Long accepted as return type"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|tzsSize
init|=
name|timezones
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|tzsExtract
init|=
name|tzsSize
operator|>
literal|0
decl_stmt|;
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|duplicate
init|=
operator|new
name|Random
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|amount
argument_list|)
decl_stmt|;
name|Calendar
name|lstart
init|=
operator|(
name|Calendar
operator|)
name|start
operator|.
name|clone
argument_list|()
decl_stmt|;
name|int
name|hours
init|=
operator|(
name|OrderDirection
operator|.
name|DESC
operator|.
name|equals
argument_list|(
name|direction
argument_list|)
operator|)
condition|?
operator|-
name|increaseBy
else|:
name|increaseBy
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|amount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tzsExtract
condition|)
block|{
name|lstart
operator|.
name|setTimeZone
argument_list|(
name|timezones
operator|.
name|get
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|tzsSize
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|returnType
operator|.
name|equals
argument_list|(
name|String
operator|.
name|class
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|ISO8601
operator|.
name|format
argument_list|(
name|lstart
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|returnType
operator|.
name|equals
argument_list|(
name|Long
operator|.
name|class
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|lstart
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|generateDuplicates
operator|&&
name|duplicate
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.1
condition|)
block|{
comment|// let's not increase the date
block|}
else|else
block|{
name|lstart
operator|.
name|add
argument_list|(
name|increaseOf
argument_list|,
name|hours
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|values
return|;
block|}
comment|/**      * @return a Calendar set for midnight of 1st January 2013      */
specifier|public
specifier|static
name|Calendar
name|midnightFirstJan2013
parameter_list|()
block|{
name|Calendar
name|c
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|c
operator|.
name|set
argument_list|(
literal|2013
argument_list|,
name|Calendar
operator|.
name|JANUARY
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
end_class

end_unit

