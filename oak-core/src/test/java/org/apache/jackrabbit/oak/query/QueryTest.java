begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|CoreValue
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
name|Result
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
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  * Test the query feature.  */
end_comment

begin_class
specifier|public
class|class
name|QueryTest
extends|extends
name|AbstractQueryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|sql1
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"sql1.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sql2
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"sql2.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sql2Explain
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"sql2_explain.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sql2_measure
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"sql2_measure.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xpath
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|"xpath.txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|bindVariableTest
parameter_list|()
throws|throws
name|Exception
block|{
name|JsopUtil
operator|.
name|apply
argument_list|(
name|root
argument_list|,
literal|"/ + \"test\": { \"hello\": {\"id\": \"1\"}, \"world\": {\"id\": \"2\"}}"
argument_list|,
name|vf
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|CoreValue
argument_list|>
name|sv
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CoreValue
argument_list|>
argument_list|()
decl_stmt|;
name|sv
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|result
decl_stmt|;
name|result
operator|=
name|executeQuery
argument_list|(
literal|"select * from [nt:base] where id = $id"
argument_list|,
name|QueryEngineImpl
operator|.
name|SQL2
argument_list|,
name|sv
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/hello"
argument_list|,
name|result
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|sv
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|executeQuery
argument_list|(
literal|"select * from [nt:base] where id = $id"
argument_list|,
name|QueryEngineImpl
operator|.
name|SQL2
argument_list|,
name|sv
argument_list|)
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/world"
argument_list|,
name|result
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|executeQuery
argument_list|(
literal|"explain select * from [nt:base] where id = 1 order by id"
argument_list|,
name|QueryEngineImpl
operator|.
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
name|assertTrue
argument_list|(
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[nt:base] as [nt:base] "
operator|+
literal|"/* traverse \"//*\" where [nt:base].[id] = cast('1' as long) */"
argument_list|,
name|result
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|(
literal|"plan"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|test
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|in
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|LineNumberReader
name|r
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|PrintWriter
name|w
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
literal|"target/"
operator|+
name|file
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|knownQueries
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|errors
init|=
literal|false
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|r
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|||
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|w
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"xpath2sql"
argument_list|)
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"xpath2sql"
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|w
operator|.
name|println
argument_list|(
literal|"xpath2sql "
operator|+
name|line
argument_list|)
expr_stmt|;
name|XPathToSQL2Converter
name|c
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
decl_stmt|;
name|String
name|got
decl_stmt|;
try|try
block|{
name|got
operator|=
name|c
operator|.
name|convert
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|executeQuery
argument_list|(
name|got
argument_list|,
name|QueryEngineImpl
operator|.
name|SQL2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|got
operator|=
literal|"invalid: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\n'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// e.printStackTrace();
name|got
operator|=
literal|"error: "
operator|+
name|e
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\n'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|knownQueries
operator|.
name|add
argument_list|(
name|line
argument_list|)
condition|)
block|{
name|got
operator|=
literal|"duplicate xpath2sql query"
expr_stmt|;
block|}
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
name|w
operator|.
name|println
argument_list|(
name|got
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|line
operator|.
name|equals
argument_list|(
name|got
argument_list|)
condition|)
block|{
name|errors
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"select"
argument_list|)
operator|||
name|line
operator|.
name|startsWith
argument_list|(
literal|"explain"
argument_list|)
operator|||
name|line
operator|.
name|startsWith
argument_list|(
literal|"measure"
argument_list|)
operator|||
name|line
operator|.
name|startsWith
argument_list|(
literal|"sql1"
argument_list|)
operator|||
name|line
operator|.
name|startsWith
argument_list|(
literal|"xpath"
argument_list|)
condition|)
block|{
name|w
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|String
name|language
init|=
name|QueryEngineImpl
operator|.
name|SQL2
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"sql1 "
argument_list|)
condition|)
block|{
name|language
operator|=
name|QueryEngineImpl
operator|.
name|SQL
expr_stmt|;
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"sql1 "
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"xpath "
argument_list|)
condition|)
block|{
name|language
operator|=
name|QueryEngineImpl
operator|.
name|XPATH
expr_stmt|;
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"xpath "
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|readEnd
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|resultLine
range|:
name|executeQuery
argument_list|(
name|line
argument_list|,
name|language
argument_list|)
control|)
block|{
name|w
operator|.
name|println
argument_list|(
name|resultLine
argument_list|)
expr_stmt|;
if|if
condition|(
name|readEnd
condition|)
block|{
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
name|errors
operator|=
literal|true
expr_stmt|;
name|readEnd
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|errors
operator|=
literal|true
expr_stmt|;
name|readEnd
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|line
operator|.
name|equals
argument_list|(
name|resultLine
argument_list|)
condition|)
block|{
name|errors
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|w
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|readEnd
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|errors
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"commit"
argument_list|)
condition|)
block|{
name|w
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"commit"
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|JsopUtil
operator|.
name|apply
argument_list|(
name|root
argument_list|,
name|line
argument_list|,
name|vf
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|w
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|errors
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Results in target/"
operator|+
name|file
operator|+
literal|" don't match expected "
operator|+
literal|"results in src/test/resources/"
operator|+
name|file
operator|+
literal|"; compare the files for details"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|executeQuery
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|language
parameter_list|)
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|Result
name|result
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
name|language
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|ResultRow
name|row
range|:
name|result
operator|.
name|getRows
argument_list|()
control|)
block|{
name|lines
operator|.
name|add
argument_list|(
name|readRow
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|query
operator|.
name|contains
argument_list|(
literal|"order by"
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|lines
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|lines
operator|.
name|add
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|lines
operator|.
name|add
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
if|if
condition|(
name|time
operator|>
literal|3000
operator|&&
operator|!
name|isDebugModeEnabled
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Query took too long: "
operator|+
name|query
operator|+
literal|" took "
operator|+
name|time
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
return|return
name|lines
return|;
block|}
specifier|private
specifier|static
name|String
name|readRow
parameter_list|(
name|ResultRow
name|row
parameter_list|)
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|CoreValue
index|[]
name|values
init|=
name|row
operator|.
name|getValues
argument_list|()
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
name|values
operator|.
name|length
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
literal|", "
argument_list|)
expr_stmt|;
block|}
name|CoreValue
name|v
init|=
name|values
index|[
name|i
index|]
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|v
operator|==
literal|null
condition|?
literal|"null"
else|:
name|v
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
specifier|private
name|Result
name|executeQuery
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|CoreValue
argument_list|>
name|sv
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|qe
operator|.
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|language
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|sv
argument_list|,
name|session
operator|.
name|getLatestRoot
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Check whether the test is running in debug mode.      *       * @return true if debug most is (most likely) enabled      */
specifier|private
specifier|static
name|boolean
name|isDebugModeEnabled
parameter_list|()
block|{
return|return
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
operator|.
name|getInputArguments
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"-agentlib:jdwp"
argument_list|)
operator|>
literal|0
return|;
block|}
block|}
end_class

end_unit

