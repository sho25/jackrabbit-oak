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
name|text
operator|.
name|ParseException
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
name|xpath
operator|.
name|XPathToSQL2Converter
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
comment|/**  * Tests the XPathToSQL2Converter  */
end_comment

begin_class
specifier|public
class|class
name|XPathTest
block|{
specifier|private
specifier|final
name|SQL2Parser
name|parser
init|=
name|SQL2ParserTest
operator|.
name|createTestSQL2Parser
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|complexQuery
parameter_list|()
throws|throws
name|ParseException
block|{
for|for
control|(
name|int
name|n
init|=
literal|1
init|;
name|n
operator|<
literal|15
condition|;
name|n
operator|++
control|)
block|{
for|for
control|(
name|int
name|m
init|=
literal|1
init|;
name|m
operator|<
literal|15
condition|;
name|m
operator|++
control|)
block|{
name|complexQuery
argument_list|(
name|n
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|complexQuery
parameter_list|(
name|int
name|n
parameter_list|,
name|int
name|m
parameter_list|)
throws|throws
name|ParseException
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"/jcr:root//*["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"and "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|m
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"or "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"@x"
operator|+
name|j
operator|+
literal|" = "
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|")\n"
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|String
name|xpath
init|=
name|buff
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|sql2
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
operator|.
name|convert
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Length: "
operator|+
name|sql2
operator|.
name|length
argument_list|()
argument_list|,
name|sql2
operator|.
name|length
argument_list|()
operator|<
literal|200000
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|parser
operator|.
name|parse
argument_list|(
name|sql2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|q
operator|.
name|buildAlternativeQuery
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|queryOptions
parameter_list|()
throws|throws
name|ParseException
block|{
name|verify
argument_list|(
literal|"(/jcr:root/a//* | /jcr:root/b//*) order by @jcr:score"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/a') "
operator|+
literal|"/* xpath: /jcr:root/a//* \n"
operator|+
literal|"order by @jcr:score */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/b') "
operator|+
literal|"/* xpath: /jcr:root/b//* "
operator|+
literal|"order by @jcr:score */ "
operator|+
literal|"order by [jcr:score]"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/a//* | /jcr:root/b//* | /jcr:root/c//*) order by @jcr:score"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/a') "
operator|+
literal|"/* xpath: /jcr:root/a//* \n"
operator|+
literal|"order by @jcr:score */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/b') "
operator|+
literal|"/* xpath: /jcr:root/b//* \n"
operator|+
literal|"order by @jcr:score */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/c') "
operator|+
literal|"/* xpath: /jcr:root/c//* "
operator|+
literal|"order by @jcr:score */ "
operator|+
literal|"order by [jcr:score]"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"//(element(*, nt:address))"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:address] as a "
operator|+
literal|"/* xpath: //element(*, nt:address) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"//(element(*, nt:address) | element(*, nt:folder))"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:address] as a "
operator|+
literal|"/* xpath: //element(*, nt:address) */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:folder] as a "
operator|+
literal|"/* xpath: // element(*, nt:folder) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(//element(*, nt:address) | //element(*, nt:folder))"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:address] as a "
operator|+
literal|"/* xpath: //element(*, nt:address) */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:folder] as a "
operator|+
literal|"/* xpath: //element(*, nt:folder) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/content//*[@a] order by @c option(traversal fail)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/content') "
operator|+
literal|"order by [c] option(traversal FAIL) "
operator|+
literal|"/* xpath: /jcr:root/content//*[@a] "
operator|+
literal|"order by @c "
operator|+
literal|"option(traversal fail) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"//*[@a or @b] order by @c option(traversal warn)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [b] is not null "
operator|+
literal|"order by [c] option(traversal WARN) "
operator|+
literal|"/* xpath: //*[@a or @b] "
operator|+
literal|"order by @c "
operator|+
literal|"option(traversal warn) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/(content|libs)//*[@a] order by @c option(traversal ok)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//*[@a] "
operator|+
literal|"order by @c option(traversal ok) */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/libs') "
operator|+
literal|"/* xpath: /jcr:root/libs//*[@a] "
operator|+
literal|"order by @c option(traversal ok) */ "
operator|+
literal|"order by [c] "
operator|+
literal|"option(traversal OK)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|chainedConditions
parameter_list|()
throws|throws
name|ParseException
block|{
name|verify
argument_list|(
literal|"/jcr:root/x[@a][@b][@c]"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and [b] is not null "
operator|+
literal|"and [c] is not null "
operator|+
literal|"and issamenode(a, '/x') "
operator|+
literal|"/* xpath: /jcr:root/x[@a][@b][@c] */"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|union
parameter_list|()
throws|throws
name|ParseException
block|{
name|verify
argument_list|(
literal|"(//*[@a=1 or @b=1] | //*[@c=1])"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] = 1 "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [b] = 1 "
operator|+
literal|"/* xpath: //*[@a=1 or @b=1] */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [c] = 1 "
operator|+
literal|"/* xpath: //*[@c=1] */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"//(a|(b|c))"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where name(a) = 'a' "
operator|+
literal|"/* xpath: //a */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where name(a) = 'b' "
operator|+
literal|"/* xpath: //b */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where name(a) = 'c' "
operator|+
literal|"/* xpath: //c */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(//*[jcr:contains(., 'some')])"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where contains(*, 'some') "
operator|+
literal|"/* xpath: //*[jcr:contains(., 'some')] */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(//*[jcr:contains(., 'x')] | //*[jcr:contains(., 'y')])"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where contains(*, 'x') "
operator|+
literal|"/* xpath: //*[jcr:contains(., 'x')] */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where contains(*, 'y') "
operator|+
literal|"/* xpath: //*[jcr:contains(., 'y')] */"
argument_list|)
expr_stmt|;
try|try
block|{
name|verify
argument_list|(
literal|"(/jcr:root/x[@a][@b][@c]"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|verify
argument_list|(
literal|"(/jcr:root/x[@a] | /jcr:root/y[@b])[@c]"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and [c] is not null "
operator|+
literal|"and issamenode(a, '/x') "
operator|+
literal|"/* xpath: /jcr:root/x[@a] [@c] */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [b] is not null "
operator|+
literal|"and [c] is not null "
operator|+
literal|"and issamenode(a, '/y') "
operator|+
literal|"/* xpath: /jcr:root/y[@b][@c] */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/x | /jcr:root/y)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/x') "
operator|+
literal|"/* xpath: /jcr:root/x */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/y') "
operator|+
literal|"/* xpath: /jcr:root/y */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/x | /jcr:root/y )"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/x') "
operator|+
literal|"/* xpath: /jcr:root/x */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/y') "
operator|+
literal|"/* xpath: /jcr:root/y */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/content//*[@a] | /jcr:root/lib//*[@b]) order by @c"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//*[@a]  "
operator|+
literal|"order by @c */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [b] is not null "
operator|+
literal|"and isdescendantnode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib//*[@b] "
operator|+
literal|"order by @c */ "
operator|+
literal|"order by [c]"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/(content|lib)/element(*, nt:base) order by @title"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content/element(*, nt:base) "
operator|+
literal|"order by @title */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib/element(*, nt:base) "
operator|+
literal|"order by @title */ "
operator|+
literal|"order by [title]"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/(content|lib)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/content|/jcr:root/lib)//*"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//* */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib//* */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/content/(a|b|c)/thumbnails/*"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/content/a/thumbnails') "
operator|+
literal|"/* xpath: /jcr:root/content/a/thumbnails/* */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/content/b/thumbnails') "
operator|+
literal|"/* xpath: /jcr:root/content/b/thumbnails/* */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/content/c/thumbnails') "
operator|+
literal|"/* xpath: /jcr:root/content/c/thumbnails/* */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/(content|lib)//*[@a]"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//*[@a] */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib//*[@a] */"
argument_list|)
expr_stmt|;
comment|// "order by @jcr:score" is kept on xpath to sql2 conversion
comment|// (because the default is ascending)
name|verify
argument_list|(
literal|"/jcr:root/content//(element(*, nt:base) | element(*, nt:folder)) order by @jcr:score"
argument_list|,
literal|"select [jcr:path], [jcr:score], * from [nt:base] as a where isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//element(*, nt:base)  order by @jcr:score */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * from [nt:folder] as a where isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content// element(*, nt:folder) order by @jcr:score */ "
operator|+
literal|"order by [jcr:score]"
argument_list|)
expr_stmt|;
comment|// "order by @jcr:score descending" is ignored on xpath to sql2 conversion
name|verify
argument_list|(
literal|"/jcr:root/content//(element(*, nt:base) | element(*, nt:folder)) order by @jcr:score descending"
argument_list|,
literal|"select [jcr:path], [jcr:score], * from [nt:base] as a where isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//element(*, nt:base)  order by @jcr:score descending */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * from [nt:folder] as a where isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content// element(*, nt:folder) order by @jcr:score descending */"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verify
parameter_list|(
name|String
name|xpath
parameter_list|,
name|String
name|expectedSql2
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|sql2
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
operator|.
name|convert
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
name|sql2
operator|=
name|formatSQL
argument_list|(
name|sql2
argument_list|)
expr_stmt|;
name|expectedSql2
operator|=
name|formatSQL
argument_list|(
name|expectedSql2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedSql2
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|sql2
argument_list|)
expr_stmt|;
block|}
specifier|static
name|String
name|formatSQL
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|sql
operator|=
name|sql
operator|.
name|replace
argument_list|(
literal|'\n'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" from "
argument_list|,
literal|"\nfrom "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" where "
argument_list|,
literal|"\nwhere "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" and "
argument_list|,
literal|"\nand "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" union "
argument_list|,
literal|"\nunion "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" order by "
argument_list|,
literal|"\norder by "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" option\\("
argument_list|,
literal|"\noption\\("
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" \\/\\* "
argument_list|,
literal|"\n/* "
argument_list|)
expr_stmt|;
return|return
name|sql
return|;
block|}
block|}
end_class

end_unit

