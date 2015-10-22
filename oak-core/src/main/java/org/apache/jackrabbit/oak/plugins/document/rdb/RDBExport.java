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
name|document
operator|.
name|rdb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
name|Set
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
name|commons
operator|.
name|json
operator|.
name|JsopBuilder
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
name|document
operator|.
name|Collection
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|NodeDocument
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
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
import|;
end_import

begin_comment
comment|/**  * Utility for dumping contents from {@link RDBDocumentStore}'s tables.  */
end_comment

begin_class
specifier|public
class|class
name|RDBExport
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
throws|,
name|UnsupportedEncodingException
throws|,
name|FileNotFoundException
block|{
name|String
name|url
init|=
literal|null
decl_stmt|,
name|user
init|=
literal|null
decl_stmt|,
name|pw
init|=
literal|null
decl_stmt|,
name|table
init|=
literal|"nodes"
decl_stmt|,
name|query
init|=
literal|null
decl_stmt|;
name|boolean
name|asArray
init|=
literal|false
decl_stmt|;
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|excl
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|excl
operator|.
name|add
argument_list|(
name|Document
operator|.
name|ID
argument_list|)
expr_stmt|;
name|RDBDocumentSerializer
name|ser
init|=
operator|new
name|RDBDocumentSerializer
argument_list|(
operator|new
name|MemoryDocumentStore
argument_list|()
argument_list|,
name|excl
argument_list|)
decl_stmt|;
name|String
name|param
init|=
literal|null
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|param
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
literal|"-u"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
operator|||
literal|"--username"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|user
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-p"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
operator|||
literal|"--password"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|pw
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-c"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
operator|||
literal|"--collection"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|table
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-j"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
operator|||
literal|"--jdbc-url"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|url
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-q"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
operator|||
literal|"--query"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|query
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-o"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
operator|||
literal|"--out"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
decl_stmt|;
name|out
operator|=
operator|new
name|PrintStream
argument_list|(
name|os
argument_list|,
literal|true
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"--jsonArray"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|asArray
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|RDBExport
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|": invalid parameter "
operator|+
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|printUsage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|RDBExport
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|": value missing for parameter "
operator|+
name|param
argument_list|)
expr_stmt|;
name|printUsage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|String
name|driver
init|=
name|RDBJDBCTools
operator|.
name|driverForDBType
argument_list|(
name|RDBJDBCTools
operator|.
name|jdbctype
argument_list|(
name|url
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|RDBExport
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|":attempt to load class "
operator|+
name|driver
operator|+
literal|" failed:"
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Connection
name|c
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|,
name|user
argument_list|,
name|pw
argument_list|)
decl_stmt|;
name|c
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Statement
name|stmt
init|=
name|c
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|String
name|sql
init|=
literal|"select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, DATA, BDATA  from "
operator|+
name|table
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|sql
operator|+=
literal|" where "
operator|+
name|query
expr_stmt|;
block|}
name|sql
operator|+=
literal|" order by id"
expr_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
if|if
condition|(
name|asArray
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
block|}
name|boolean
name|needComma
init|=
name|asArray
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|String
name|id
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"ID"
argument_list|)
decl_stmt|;
name|long
name|modified
init|=
name|rs
operator|.
name|getLong
argument_list|(
literal|"MODIFIED"
argument_list|)
decl_stmt|;
name|long
name|modcount
init|=
name|rs
operator|.
name|getLong
argument_list|(
literal|"MODCOUNT"
argument_list|)
decl_stmt|;
name|long
name|cmodcount
init|=
name|rs
operator|.
name|getLong
argument_list|(
literal|"CMODCOUNT"
argument_list|)
decl_stmt|;
name|long
name|hasBinary
init|=
name|rs
operator|.
name|getLong
argument_list|(
literal|"HASBINARY"
argument_list|)
decl_stmt|;
name|long
name|deletedOnce
init|=
name|rs
operator|.
name|getLong
argument_list|(
literal|"DELETEDONCE"
argument_list|)
decl_stmt|;
name|String
name|data
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"DATA"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bdata
init|=
name|rs
operator|.
name|getBytes
argument_list|(
literal|"BDATA"
argument_list|)
decl_stmt|;
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
name|id
argument_list|,
name|hasBinary
operator|==
literal|1
argument_list|,
name|deletedOnce
operator|==
literal|1
argument_list|,
name|modified
argument_list|,
name|modcount
argument_list|,
name|cmodcount
argument_list|,
name|data
argument_list|,
name|bdata
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
decl_stmt|;
name|String
name|docjson
init|=
name|ser
operator|.
name|asString
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|StringBuilder
name|fulljson
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|fulljson
operator|.
name|append
argument_list|(
literal|"{\"_id\":\""
argument_list|)
expr_stmt|;
name|JsopBuilder
operator|.
name|escape
argument_list|(
name|id
argument_list|,
name|fulljson
argument_list|)
expr_stmt|;
name|fulljson
operator|.
name|append
argument_list|(
literal|"\","
argument_list|)
expr_stmt|;
name|fulljson
operator|.
name|append
argument_list|(
name|docjson
argument_list|)
expr_stmt|;
name|fulljson
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
if|if
condition|(
name|asArray
operator|&&
name|needComma
operator|&&
operator|!
name|rs
operator|.
name|isLast
argument_list|()
condition|)
block|{
name|fulljson
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|fulljson
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|asArray
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|RDBExport
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" [-j/--jdbc-url JDBC-URL] [-u/--username username] [-p/--password password] [-c/--collection table] [-q/--query query][--jsonArray]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

