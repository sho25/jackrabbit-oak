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
name|jcr
operator|.
name|query
package|;
end_package

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
name|core
operator|.
name|query
operator|.
name|AbstractQueryTest
import|;
end_import

begin_comment
comment|/**  * Tests the fulltext index.  */
end_comment

begin_class
specifier|public
class|class
name|QueryFulltextTest
extends|extends
name|AbstractQueryTest
block|{
specifier|public
name|void
name|testFulltext
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|superuser
decl_stmt|;
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
name|Node
name|n1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node1"
argument_list|)
decl_stmt|;
name|n1
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node2"
argument_list|)
decl_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hallo"
argument_list|)
expr_stmt|;
name|Node
name|n3
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node3"
argument_list|)
decl_stmt|;
name|n3
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hello hallo"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|sql2
init|=
literal|"select [jcr:path] as [path] from [nt:base] "
operator|+
literal|"where contains([text], 'hello OR hallo') order by [jcr:path]"
decl_stmt|;
name|Query
name|q
decl_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
comment|// TODO the plan should actually be:
comment|//            assertEquals("[nt:base] as [nt:base] /* " +
comment|//                    "+((text:hallo text:hello)~1) +text:{* TO *} " +
comment|//                    "ft:(text:\"hallo\" OR text:\"hello\") " +
comment|//                    "where contains([nt:base].[text], cast('hello OR hallo' as string)) */",
comment|//                    getResult(q.execute(), "plan"));
name|assertEquals
argument_list|(
literal|"[nt:base] as [nt:base] /* "
operator|+
literal|"aggregate :fulltext:hallo :fulltext:hello "
operator|+
literal|"ft:(text:\"hallo\" OR text:\"hello\") "
operator|+
literal|"where contains([nt:base].[text], cast('hello OR hallo' as string)) */"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"plan"
argument_list|)
argument_list|)
expr_stmt|;
comment|// lowercase "or" mean search for the term "or"
name|sql2
operator|=
literal|"select [jcr:path] as [path] from [nt:base] "
operator|+
literal|"where contains([text], 'hello or hallo') order by [jcr:path]"
expr_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFulltextRelativeProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|superuser
decl_stmt|;
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
name|Node
name|n1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node1"
argument_list|)
decl_stmt|;
name|n1
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node2"
argument_list|)
decl_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hallo"
argument_list|)
expr_stmt|;
name|Node
name|n3
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node3"
argument_list|)
decl_stmt|;
name|n3
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hello hallo"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Query
name|q
decl_stmt|;
name|String
name|sql2
init|=
literal|"select [jcr:path] as [path] from [nt:base] "
operator|+
literal|"where ISCHILDNODE([/testroot])"
operator|+
literal|" AND CONTAINS(text, 'hallo')"
decl_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
comment|// TODO the plan should actually be:
comment|//          assertEquals("[nt:base] as [nt:base] /* " +
comment|//                  "+text:hallo +:path:/testroot/* +text:{* TO *} " +
comment|//                  "ft:(text:\"hallo\") " +
comment|//                  "where (ischildnode([nt:base], [/testroot])) " +
comment|//                  "and (contains([nt:base].[text], cast('hallo' as string))) */",
comment|//                  getResult(q.execute(), "plan"));
name|assertEquals
argument_list|(
literal|"[nt:base] as [nt:base] /* "
operator|+
literal|"aggregate +:fulltext:hallo* +:path:/testroot/* +text:{* TO *}"
operator|+
literal|"ft:(text:\"hallo\") "
operator|+
literal|"where (ischildnode([nt:base], [/testroot])) "
operator|+
literal|"and (contains([nt:base].[text], cast('hallo' as string))) */"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"plan"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/testroot/node2, /testroot/node3"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
name|sql2
operator|=
literal|"select [jcr:path] as [path] from [nt:base] "
operator|+
literal|"where contains([node1/text], 'hello') order by [jcr:path]"
expr_stmt|;
comment|//      q = qm.createQuery("explain " + sql2, Query.JCR_SQL2);
comment|//    assertEquals("[nt:base] as [nt:base] /* " +
comment|//            "+text:hallo +:path:/testroot/* +text:{* TO *} " +
comment|//            "ft:(text:\"hallo\") " +
comment|//            "where (ischildnode([nt:base], [/testroot])) " +
comment|//            "and (contains([nt:base].[text], cast('hallo' as string))) */",
comment|//            getResult(q.execute(), "plan"));
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/testroot"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
name|sql2
operator|=
literal|"select [jcr:path] as [path] from [nt:base] "
operator|+
literal|"where contains([node2/text], 'hello OR hallo') order by [jcr:path]"
expr_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
comment|// TODO the plan should actually be:
comment|//            assertEquals("[nt:base] as [nt:base] /* " +
comment|//                    "(text:hallo text:hello)~1 " +
comment|//                    "ft:(node2/text:\"hallo\" OR node2/text:\"hello\") " +
comment|//                    "parent:node2 " +
comment|//                    "where contains([nt:base].[node2/text], cast('hello OR hallo' as string)) */",
comment|//                    getResult(q.execute(), "plan"));
name|assertEquals
argument_list|(
literal|"[nt:base] as [nt:base] /* "
operator|+
literal|"aggregate :fulltext:hallo* :fulltext:hello* "
operator|+
literal|"ft:(node2/text:\"hallo\" OR node2/text:\"hello\") "
operator|+
literal|"parent:node2 "
operator|+
literal|"where contains([nt:base].[node2/text], cast('hello OR hallo' as string)) */"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"plan"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/testroot"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
name|sql2
operator|=
literal|"select [jcr:path] as [path] from [nt:base] "
operator|+
literal|"where contains([node1/text], 'hello') "
operator|+
literal|"and contains([node2/text], 'hallo') "
operator|+
literal|"order by [jcr:path]"
expr_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
comment|// TODO OAK-890
name|assertEquals
argument_list|(
literal|"[nt:base] as [nt:base] /* "
operator|+
literal|"aggregate Not yet implemented "
operator|+
literal|"where (contains([nt:base].[node1/text], cast('hello' as string))) "
operator|+
literal|"and (contains([nt:base].[node2/text], cast('hallo' as string))) */"
argument_list|,
name|getResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
literal|"plan"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|sql2
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
comment|// assertEquals("/testroot",
comment|//        getResult(q.execute(), "path"));
block|}
specifier|static
name|String
name|getResult
parameter_list|(
name|QueryResult
name|result
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|RowIterator
name|it
init|=
name|result
operator|.
name|getRows
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|buff
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|it
operator|.
name|nextRow
argument_list|()
operator|.
name|getValue
argument_list|(
name|propertyName
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

