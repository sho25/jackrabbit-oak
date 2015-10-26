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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|IOException
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|ResultSetMetaData
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
name|sql
operator|.
name|Types
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|DocumentStoreException
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
name|OakVersion
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
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
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
name|IOException
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
decl_stmt|,
name|dumpfile
init|=
literal|null
decl_stmt|,
name|lobdir
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
literal|"--from-db2-dump"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|dumpfile
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
literal|"--lobdir"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|lobdir
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
elseif|else
if|if
condition|(
literal|"--version"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
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
literal|" version "
operator|+
name|OakVersion
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"--help"
operator|.
name|equals
argument_list|(
name|param
argument_list|)
condition|)
block|{
name|printHelp
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
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
if|if
condition|(
name|dumpfile
operator|!=
literal|null
operator|&&
name|url
operator|!=
literal|null
condition|)
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
literal|": must use either dump file or JDBC URL"
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
elseif|else
if|if
condition|(
name|dumpfile
operator|!=
literal|null
condition|)
block|{
name|dumpFile
argument_list|(
name|dumpfile
argument_list|,
name|lobdir
argument_list|,
name|asArray
argument_list|,
name|out
argument_list|,
name|ser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dumpJDBC
argument_list|(
name|url
argument_list|,
name|user
argument_list|,
name|pw
argument_list|,
name|table
argument_list|,
name|query
argument_list|,
name|asArray
argument_list|,
name|out
argument_list|,
name|ser
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|dumpFile
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|lobdir
parameter_list|,
name|boolean
name|asArray
parameter_list|,
name|PrintStream
name|out
parameter_list|,
name|RDBDocumentSerializer
name|ser
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|File
name|lobDirectory
init|=
name|lobdir
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
name|f
operator|.
name|getParentFile
argument_list|()
argument_list|,
literal|"lobdir"
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|lobdir
argument_list|)
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|InputStreamReader
name|ir
init|=
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
name|UTF8
argument_list|)
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
name|ir
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
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|parseDel
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|smodified
init|=
name|fields
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|shasbinary
init|=
name|fields
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|sdeletedonce
init|=
name|fields
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|String
name|smodcount
init|=
name|fields
operator|.
name|get
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|String
name|scmodcount
init|=
name|fields
operator|.
name|get
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|sdata
init|=
name|fields
operator|.
name|get
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|String
name|sbdata
init|=
name|fields
operator|.
name|get
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sbdata
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|String
name|lobfile
init|=
name|sbdata
operator|.
name|replace
argument_list|(
literal|"/"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|int
name|lastdot
init|=
name|lobfile
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
name|String
name|length
init|=
name|lobfile
operator|.
name|substring
argument_list|(
name|lastdot
operator|+
literal|1
argument_list|)
decl_stmt|;
name|lobfile
operator|=
name|lobfile
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lastdot
argument_list|)
expr_stmt|;
name|lastdot
operator|=
name|lobfile
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|String
name|startpos
init|=
name|lobfile
operator|.
name|substring
argument_list|(
name|lastdot
operator|+
literal|1
argument_list|)
decl_stmt|;
name|lobfile
operator|=
name|lobfile
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lastdot
argument_list|)
expr_stmt|;
name|int
name|s
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|startpos
argument_list|)
decl_stmt|;
name|int
name|l
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|File
name|lf
init|=
operator|new
name|File
argument_list|(
name|lobDirectory
argument_list|,
name|lobfile
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|lf
argument_list|)
decl_stmt|;
name|bytes
operator|=
operator|new
name|byte
index|[
name|l
index|]
expr_stmt|;
name|IOUtils
operator|.
name|skip
argument_list|(
name|is
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|read
argument_list|(
name|is
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
name|id
argument_list|,
literal|"1"
operator|.
name|equals
argument_list|(
name|shasbinary
argument_list|)
argument_list|,
literal|"1"
operator|.
name|equals
argument_list|(
name|sdeletedonce
argument_list|)
argument_list|,
name|smodified
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|0
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|smodified
argument_list|)
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|smodcount
argument_list|)
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|scmodcount
argument_list|)
argument_list|,
name|sdata
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|StringBuilder
name|fulljson
init|=
name|dumpRow
argument_list|(
name|ser
argument_list|,
name|id
argument_list|,
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|asArray
operator|&&
name|needComma
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
catch|catch
parameter_list|(
name|DocumentStoreException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error: skipping line for ID "
operator|+
name|id
operator|+
literal|" because of "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
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
block|}
specifier|private
specifier|static
name|ArrayList
argument_list|<
name|String
argument_list|>
name|parseDel
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|inQuoted
init|=
literal|false
decl_stmt|;
name|char
name|quotechar
init|=
literal|'"'
decl_stmt|;
name|char
name|fielddelim
init|=
literal|','
decl_stmt|;
name|StringBuilder
name|value
init|=
operator|new
name|StringBuilder
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
name|line
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|line
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|inQuoted
condition|)
block|{
if|if
condition|(
name|c
operator|==
name|fielddelim
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|c
operator|==
name|quotechar
condition|)
block|{
name|inQuoted
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|value
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|c
operator|==
name|quotechar
condition|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|!=
name|line
operator|.
name|length
argument_list|()
operator|&&
name|line
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
name|quotechar
condition|)
block|{
comment|// quoted quote char
name|value
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|i
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|inQuoted
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|value
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|.
name|add
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|void
name|dumpJDBC
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|pw
parameter_list|,
name|String
name|table
parameter_list|,
name|String
name|query
parameter_list|,
name|boolean
name|asArray
parameter_list|,
name|PrintStream
name|out
parameter_list|,
name|RDBDocumentSerializer
name|ser
parameter_list|)
throws|throws
name|SQLException
block|{
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
name|ResultSetMetaData
name|rsm
init|=
literal|null
decl_stmt|;
name|boolean
name|idIsAscii
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|rsm
operator|==
literal|null
condition|)
block|{
name|rsm
operator|=
name|rs
operator|.
name|getMetaData
argument_list|()
expr_stmt|;
name|idIsAscii
operator|=
operator|!
name|isBinaryType
argument_list|(
name|rsm
operator|.
name|getColumnType
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|id
init|=
name|idIsAscii
condition|?
name|rs
operator|.
name|getString
argument_list|(
literal|"ID"
argument_list|)
else|:
operator|new
name|String
argument_list|(
name|rs
operator|.
name|getBytes
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
name|UTF8
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
name|StringBuilder
name|fulljson
init|=
name|dumpRow
argument_list|(
name|ser
argument_list|,
name|id
argument_list|,
name|row
argument_list|)
decl_stmt|;
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
name|StringBuilder
name|dumpRow
parameter_list|(
name|RDBDocumentSerializer
name|ser
parameter_list|,
name|String
name|id
parameter_list|,
name|RDBRow
name|row
parameter_list|)
block|{
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
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fulljson
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isBinaryType
parameter_list|(
name|int
name|sqlType
parameter_list|)
block|{
return|return
name|sqlType
operator|==
name|Types
operator|.
name|VARBINARY
operator|||
name|sqlType
operator|==
name|Types
operator|.
name|BINARY
operator|||
name|sqlType
operator|==
name|Types
operator|.
name|LONGVARBINARY
return|;
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
literal|" -j/--jdbc-url JDBC-URL [-u/--username username] [-p/--password password] [-c/--collection table] [-q/--query query] [-o/--out file] [--jsonArray]"
argument_list|)
expr_stmt|;
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
literal|" --from-DB2-dump file [--lobdir lobdir] [-o/--out file] [--jsonArray]"
argument_list|)
expr_stmt|;
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
literal|" --version"
argument_list|)
expr_stmt|;
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
literal|" --help"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|printHelp
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Export Apache OAK RDB data to JSON files"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Generic options:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  --help                             produce this help message"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  --version                          show version information"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"JDBC options:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  -j/--jdbc-url JDBC-URL             JDBC URL of database to connect to"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  -u/--username username             database username"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  -p/--password password             database password"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  -c/--collection table              table name (defaults to 'nodes')"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  -q/--query query                   SQL where clause (minus 'where')"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Dump file options:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  --from-DB2-dump file               name of DB2 DEL export file"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  --lobdir dir                       name of DB2 DEL export file LOB directory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"                                     (defaults to ./lobdir under the dump file)"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Output options:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  -o/--out file                      Output to name file (instead of stdout)"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  --jsonArray                        Output a JSON array (instead of one JSON doc per line)"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

