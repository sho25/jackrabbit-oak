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
name|query
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
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
operator|.
name|JCR_SQL2
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
name|JCR_PRIMARYTYPE
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
name|Type
operator|.
name|NAME
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
name|query
operator|.
name|QueryEngineImpl
operator|.
name|QuerySelectionMode
operator|.
name|ALTERNATIVE
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
name|query
operator|.
name|QueryEngineImpl
operator|.
name|QuerySelectionMode
operator|.
name|CHEAPEST
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
name|query
operator|.
name|QueryEngineImpl
operator|.
name|QuerySelectionMode
operator|.
name|ORIGINAL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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
name|assertNotSame
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
name|assertTrue
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
name|List
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
name|Oak
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
name|ContentRepository
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
name|QueryEngine
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
name|namepath
operator|.
name|LocalNameMapper
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
name|namepath
operator|.
name|NamePathMapperImpl
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
name|MemoryNodeStore
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
name|ast
operator|.
name|NodeTypeInfoProvider
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
name|OpenSecurityProvider
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
name|NodeStore
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
comment|/**  * aim to cover the various aspects of Query.optimise()  */
end_comment

begin_class
specifier|public
class|class
name|SQL2OptimiseQueryTest
extends|extends
name|AbstractQueryTest
block|{
specifier|private
name|NodeStore
name|store
decl_stmt|;
specifier|private
name|QueryEngineSettings
name|qeSettings
init|=
operator|new
name|QueryEngineSettings
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSql2Optimisation
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|/**      * checks the {@code Query#optimise()} calls for the conversion from OR to UNION from a query      * POV; ensuring that it returns always the same, expected resultset.      *       * @throws RepositoryException      * @throws CommitFailedException      */
annotation|@
name|Test
specifier|public
name|void
name|orToUnions
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|CommitFailedException
block|{
name|Tree
name|test
decl_stmt|,
name|t
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|original
decl_stmt|,
name|optimised
decl_stmt|,
name|cheapest
decl_stmt|,
name|expected
decl_stmt|;
name|String
name|statement
decl_stmt|;
name|test
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|=
name|addChildWithProperty
argument_list|(
name|test
argument_list|,
literal|"a"
argument_list|,
literal|"p"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
literal|"a1"
argument_list|)
expr_stmt|;
name|t
operator|=
name|addChildWithProperty
argument_list|(
name|test
argument_list|,
literal|"b"
argument_list|,
literal|"p"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
literal|"b1"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"p2"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|t
operator|=
name|addChildWithProperty
argument_list|(
name|test
argument_list|,
literal|"c"
argument_list|,
literal|"p"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"p3"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|addChildWithProperty
argument_list|(
name|test
argument_list|,
literal|"d"
argument_list|,
literal|"p"
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
name|addChildWithProperty
argument_list|(
name|test
argument_list|,
literal|"e"
argument_list|,
literal|"p"
argument_list|,
literal|"e"
argument_list|)
expr_stmt|;
name|test
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|addChildWithProperty
argument_list|(
name|test
argument_list|,
literal|"a"
argument_list|,
literal|"p"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|statement
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"SELECT * FROM [%s] WHERE p = 'a' OR p = 'b'"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|expected
operator|=
name|of
argument_list|(
literal|"/test/a"
argument_list|,
literal|"/test/b"
argument_list|,
literal|"/test2/a"
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ORIGINAL
argument_list|)
expr_stmt|;
name|original
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ALTERNATIVE
argument_list|)
expr_stmt|;
name|optimised
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|CHEAPEST
argument_list|)
expr_stmt|;
name|cheapest
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertOrToUnionResults
argument_list|(
name|expected
argument_list|,
name|original
argument_list|,
name|optimised
argument_list|,
name|cheapest
argument_list|)
expr_stmt|;
name|statement
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"SELECT * FROM [%s] WHERE p = 'a' OR p = 'b' OR p = 'c' OR p = 'd' OR p = 'e' "
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|expected
operator|=
name|of
argument_list|(
literal|"/test/a"
argument_list|,
literal|"/test/b"
argument_list|,
literal|"/test/c"
argument_list|,
literal|"/test/d"
argument_list|,
literal|"/test/e"
argument_list|,
literal|"/test2/a"
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ORIGINAL
argument_list|)
expr_stmt|;
name|original
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ALTERNATIVE
argument_list|)
expr_stmt|;
name|optimised
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|CHEAPEST
argument_list|)
expr_stmt|;
name|cheapest
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertOrToUnionResults
argument_list|(
name|expected
argument_list|,
name|original
argument_list|,
name|optimised
argument_list|,
name|cheapest
argument_list|)
expr_stmt|;
name|statement
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"SELECT * FROM [%s] WHERE (p = 'a' OR p = 'b') AND (p1 = 'a1' OR p1 = 'b1')"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|expected
operator|=
name|of
argument_list|(
literal|"/test/a"
argument_list|,
literal|"/test/b"
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ORIGINAL
argument_list|)
expr_stmt|;
name|original
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ALTERNATIVE
argument_list|)
expr_stmt|;
name|optimised
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|CHEAPEST
argument_list|)
expr_stmt|;
name|cheapest
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertOrToUnionResults
argument_list|(
name|expected
argument_list|,
name|original
argument_list|,
name|optimised
argument_list|,
name|cheapest
argument_list|)
expr_stmt|;
name|statement
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"SELECT * FROM [%s] WHERE (p = 'a' AND p1 = 'a1') OR (p = 'b' AND p1 = 'b1')"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|expected
operator|=
name|of
argument_list|(
literal|"/test/a"
argument_list|,
literal|"/test/b"
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ORIGINAL
argument_list|)
expr_stmt|;
name|original
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ALTERNATIVE
argument_list|)
expr_stmt|;
name|optimised
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|CHEAPEST
argument_list|)
expr_stmt|;
name|cheapest
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertOrToUnionResults
argument_list|(
name|expected
argument_list|,
name|original
argument_list|,
name|optimised
argument_list|,
name|cheapest
argument_list|)
expr_stmt|;
name|statement
operator|=
literal|"SELECT * FROM [oak:Unstructured] AS c "
operator|+
literal|"WHERE ( c.[p] = 'a' "
operator|+
literal|"OR c.[p2] = 'a' "
operator|+
literal|"OR c.[p3] = 'a') "
operator|+
literal|"AND ISDESCENDANTNODE(c, '/test') "
operator|+
literal|"ORDER BY added DESC"
expr_stmt|;
name|expected
operator|=
name|of
argument_list|(
literal|"/test/a"
argument_list|,
literal|"/test/b"
argument_list|,
literal|"/test/c"
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ORIGINAL
argument_list|)
expr_stmt|;
name|original
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|ALTERNATIVE
argument_list|)
expr_stmt|;
name|optimised
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setQuerySelectionMode
argument_list|(
name|CHEAPEST
argument_list|)
expr_stmt|;
name|cheapest
operator|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|JCR_SQL2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertOrToUnionResults
argument_list|(
name|expected
argument_list|,
name|original
argument_list|,
name|optimised
argument_list|,
name|cheapest
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertOrToUnionResults
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|,
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|original
parameter_list|,
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|optimised
parameter_list|,
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|cheapest
parameter_list|)
block|{
comment|// checks that all the three list are the expected content
name|assertThat
argument_list|(
name|checkNotNull
argument_list|(
name|original
argument_list|)
argument_list|,
name|is
argument_list|(
name|checkNotNull
argument_list|(
name|expected
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkNotNull
argument_list|(
name|optimised
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkNotNull
argument_list|(
name|cheapest
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that all the three lists contains the same. Paranoid but still a fast check
name|assertThat
argument_list|(
name|original
argument_list|,
name|is
argument_list|(
name|optimised
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|optimised
argument_list|,
name|is
argument_list|(
name|cheapest
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cheapest
argument_list|,
name|is
argument_list|(
name|original
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Tree
name|addChildWithProperty
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|father
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|String
name|propName
parameter_list|,
annotation|@
name|Nonnull
name|String
name|propValue
parameter_list|)
block|{
name|Tree
name|t
init|=
name|checkNotNull
argument_list|(
name|father
argument_list|)
operator|.
name|addChild
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|checkNotNull
argument_list|(
name|propName
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|propValue
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
comment|/**      * ensure that an optimisation is available for the provided queries.      *       * @throws ParseException      */
annotation|@
name|Test
specifier|public
name|void
name|optimise
parameter_list|()
throws|throws
name|ParseException
block|{
name|SQL2Parser
name|parser
init|=
operator|new
name|SQL2Parser
argument_list|(
name|getMappings
argument_list|()
argument_list|,
name|getNodeTypes
argument_list|()
argument_list|,
name|qeSettings
argument_list|)
decl_stmt|;
name|String
name|statement
decl_stmt|;
name|Query
name|original
decl_stmt|,
name|optimised
decl_stmt|;
name|statement
operator|=
literal|"SELECT * FROM [nt:unstructured] AS c "
operator|+
literal|"WHERE "
operator|+
literal|"(c.[p1]='a' OR c.[p2]='b') "
expr_stmt|;
name|original
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|statement
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|optimised
operator|=
name|original
operator|.
name|buildAlternativeQuery
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|optimised
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|original
argument_list|,
name|optimised
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|optimised
operator|instanceof
name|UnionQueryImpl
argument_list|)
expr_stmt|;
name|statement
operator|=
literal|"SELECT * FROM [nt:unstructured] AS c "
operator|+
literal|"WHERE "
operator|+
literal|"(c.[p1]='a' OR c.[p2]='b') "
operator|+
literal|"AND "
operator|+
literal|"ISDESCENDANTNODE(c, '/test') "
expr_stmt|;
name|original
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|statement
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|optimised
operator|=
name|original
operator|.
name|buildAlternativeQuery
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|optimised
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|original
argument_list|,
name|optimised
argument_list|)
expr_stmt|;
name|statement
operator|=
literal|"SELECT * FROM [nt:unstructured] AS c "
operator|+
literal|"WHERE "
operator|+
literal|"(c.[p1]='a' OR c.[p2]='b' OR c.[p3]='c') "
operator|+
literal|"AND "
operator|+
literal|"ISDESCENDANTNODE(c, '/test') "
expr_stmt|;
name|original
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|statement
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|optimised
operator|=
name|original
operator|.
name|buildAlternativeQuery
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|optimised
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|original
argument_list|,
name|optimised
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NamePathMapper
name|getMappings
parameter_list|()
block|{
return|return
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|root
argument_list|,
name|QueryEngine
operator|.
name|NO_MAPPINGS
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeTypeInfoProvider
name|getNodeTypes
parameter_list|()
block|{
return|return
operator|new
name|NodeStateNodeTypeInfoProvider
argument_list|(
name|INITIAL_CONTENT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|store
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
return|return
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|qeSettings
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
block|}
end_class

end_unit

