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
name|lucene
package|;
end_package

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
name|index
operator|.
name|lucene
operator|.
name|TestUtil
operator|.
name|useV2
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
name|List
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
name|query
operator|.
name|QueryEngineSettings
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
name|Lists
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexQueryTestSQL2Optimisation
extends|extends
name|LuceneIndexQueryTest
block|{
annotation|@
name|Override
name|Oak
name|getOakRepo
parameter_list|()
block|{
return|return
name|super
operator|.
name|getOakRepo
argument_list|()
operator|.
name|with
argument_list|(
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
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|oak2660
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|name
init|=
literal|"name"
decl_stmt|;
specifier|final
name|String
name|surname
init|=
literal|"surname"
decl_stmt|;
specifier|final
name|String
name|description
init|=
literal|"description"
decl_stmt|;
specifier|final
name|String
name|added
init|=
literal|"added"
decl_stmt|;
specifier|final
name|String
name|yes
init|=
literal|"yes"
decl_stmt|;
name|Tree
name|t
decl_stmt|;
comment|// re-define the lucene index
name|t
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/"
operator|+
name|TEST_INDEX_NAME
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/"
operator|+
name|TEST_INDEX_NAME
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|Tree
name|indexDefn
init|=
name|createTestIndexNode
argument_list|(
name|t
argument_list|,
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
argument_list|)
decl_stmt|;
name|useV2
argument_list|(
name|indexDefn
argument_list|)
expr_stmt|;
name|indexDefn
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|TEST_MODE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tree
name|props
init|=
name|TestUtil
operator|.
name|newRulePropTree
argument_list|(
name|indexDefn
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|enablePropertyIndex
argument_list|(
name|props
argument_list|,
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|enableForFullText
argument_list|(
name|props
argument_list|,
name|surname
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|enableForFullText
argument_list|(
name|props
argument_list|,
name|description
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|enableForOrdered
argument_list|(
name|props
argument_list|,
name|added
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// creating the dataset
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|t
operator|=
name|content
operator|.
name|addChild
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|surname
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|description
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|added
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|content
operator|.
name|addChild
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|surname
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|description
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|added
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|content
operator|.
name|addChild
argument_list|(
literal|"test3"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|surname
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|description
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|added
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|content
operator|.
name|addChild
argument_list|(
literal|"test4"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|surname
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|description
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|added
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|content
operator|.
name|addChild
argument_list|(
literal|"test5"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|surname
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|description
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|added
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
name|content
operator|.
name|addChild
argument_list|(
literal|"test6"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|surname
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|description
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|added
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// asserting the initial state
for|for
control|(
name|String
name|s
range|:
name|expected
control|)
block|{
name|assertTrue
argument_list|(
literal|"wrong initial state"
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|s
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|statement
init|=
literal|"SELECT * "
operator|+
literal|"FROM ["
operator|+
name|NT_UNSTRUCTURED
operator|+
literal|"] AS c "
operator|+
literal|"WHERE "
operator|+
literal|"( "
operator|+
literal|"c.["
operator|+
name|name
operator|+
literal|"] = '"
operator|+
name|yes
operator|+
literal|"' "
operator|+
literal|"OR CONTAINS(c.["
operator|+
name|surname
operator|+
literal|"], '"
operator|+
name|yes
operator|+
literal|"') "
operator|+
literal|"OR CONTAINS(c.["
operator|+
name|description
operator|+
literal|"], '"
operator|+
name|yes
operator|+
literal|"') "
operator|+
literal|") "
operator|+
literal|"AND ISDESCENDANTNODE(c, '"
operator|+
name|content
operator|.
name|getPath
argument_list|()
operator|+
literal|"') "
operator|+
literal|"ORDER BY "
operator|+
name|added
operator|+
literal|" DESC "
decl_stmt|;
name|assertQuery
argument_list|(
name|statement
argument_list|,
name|SQL2
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

