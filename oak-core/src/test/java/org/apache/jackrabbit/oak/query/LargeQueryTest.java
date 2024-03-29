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
name|Random
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

begin_class
specifier|public
class|class
name|LargeQueryTest
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
name|testSimpleOr
parameter_list|()
throws|throws
name|ParseException
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"//*["
argument_list|)
decl_stmt|;
name|StringBuilder
name|buff2
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"select [jcr:path], [jcr:score], * from [nt:base] as a where [x] in("
argument_list|)
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
literal|100000
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
literal|" or "
argument_list|)
expr_stmt|;
name|buff2
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
literal|"@x="
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|buff2
operator|.
name|append
argument_list|(
name|i
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
name|buff2
operator|.
name|append
argument_list|(
literal|")"
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
name|XPathToSQL2Converter
name|conv
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
decl_stmt|;
name|String
name|sql2
init|=
name|conv
operator|.
name|convert
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
name|buff2
operator|.
name|append
argument_list|(
literal|" /* xpath: "
argument_list|)
operator|.
name|append
argument_list|(
name|xpath
argument_list|)
operator|.
name|append
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buff2
operator|.
name|toString
argument_list|()
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCombinedOr
parameter_list|()
throws|throws
name|ParseException
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"//*["
argument_list|)
decl_stmt|;
name|StringBuilder
name|buff2
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"select [jcr:path], [jcr:score], * from [nt:base] as a where [x] in("
argument_list|)
decl_stmt|;
name|int
name|step
init|=
literal|111
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
name|step
operator|==
literal|2
condition|)
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
literal|" or "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"@x>"
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|buff2
operator|.
name|append
argument_list|(
literal|") union select [jcr:path], [jcr:score], * from [nt:base] as a "
operator|+
literal|"where [x]> "
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|buff2
operator|.
name|append
argument_list|(
literal|" union select [jcr:path], [jcr:score], * from [nt:base] as a "
operator|+
literal|"where [x] in("
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|" or "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"@x="
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
name|step
operator|!=
literal|3
condition|)
block|{
name|buff2
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buff2
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|buff2
operator|.
name|append
argument_list|(
literal|")"
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
name|XPathToSQL2Converter
name|conv
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
decl_stmt|;
name|String
name|sql2
init|=
name|conv
operator|.
name|convert
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
name|buff2
operator|.
name|append
argument_list|(
literal|" /* xpath: "
argument_list|)
operator|.
name|append
argument_list|(
name|xpath
argument_list|)
operator|.
name|append
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|buff2
operator|.
name|toString
argument_list|()
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRandomizedCondition
parameter_list|()
throws|throws
name|ParseException
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|testRandomizedCondition
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testRandomizedCondition
parameter_list|(
name|int
name|seed
parameter_list|)
throws|throws
name|ParseException
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"//*["
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|randomCondition
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
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
name|XPathToSQL2Converter
name|conv
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
decl_stmt|;
name|String
name|sql2
init|=
name|conv
operator|.
name|convert
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
name|int
name|xpathIndex
init|=
name|sql2
operator|.
name|lastIndexOf
argument_list|(
literal|" /* xpath: "
argument_list|)
decl_stmt|;
name|sql2
operator|=
name|sql2
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|xpathIndex
argument_list|)
expr_stmt|;
comment|// should use union now
name|assertTrue
argument_list|(
name|sql2
operator|.
name|indexOf
argument_list|(
literal|" or "
argument_list|)
operator|<
literal|0
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
specifier|private
name|String
name|randomCondition
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
switch|switch
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|14
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
return|return
literal|"@"
operator|+
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
argument_list|)
return|;
case|case
literal|2
case|:
case|case
literal|3
case|:
return|return
literal|"@"
operator|+
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|+
literal|"="
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
return|;
case|case
literal|4
case|:
return|return
literal|"@"
operator|+
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|+
literal|">"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
return|;
case|case
literal|5
case|:
return|return
literal|"jcr:contains(., 'x')"
return|;
case|case
literal|6
case|:
case|case
literal|7
case|:
return|return
name|randomCondition
argument_list|(
name|r
argument_list|)
operator|+
literal|" or "
operator|+
name|randomCondition
argument_list|(
name|r
argument_list|)
return|;
case|case
literal|8
case|:
case|case
literal|9
case|:
return|return
name|randomCondition
argument_list|(
name|r
argument_list|)
operator|+
literal|" and "
operator|+
name|randomCondition
argument_list|(
name|r
argument_list|)
return|;
case|case
literal|10
case|:
return|return
literal|"("
operator|+
name|randomCondition
argument_list|(
name|r
argument_list|)
operator|+
literal|")"
return|;
case|case
literal|11
case|:
return|return
literal|"@jcr:primaryType='nt:base'"
return|;
case|case
literal|12
case|:
return|return
literal|"@jcr:primaryType='nt:file'"
return|;
case|case
literal|13
case|:
return|return
literal|"@jcr:primaryType='nt:folder'"
return|;
case|case
literal|14
case|:
comment|// return "not(" + randomCondition(r) + ")";
block|}
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit

