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
name|Assume
operator|.
name|assumeTrue
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
name|PreparedStatement
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
name|Types
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|AbstractDocumentStoreTest
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
name|DocumentStoreFixture
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
name|DocumentStorePerformanceTest
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

begin_comment
comment|/**  * Tests measuring the performance of various {@link RDBDocumentStore}  * operations.  *<p>  * These tests are disabled by default due to their long running time. On the command line  * specify {@code -DRDBDocumentStorePerformanceTest=true} to enable them.  */
end_comment

begin_class
specifier|public
class|class
name|RDBDocumentStorePerformanceTest
extends|extends
name|AbstractDocumentStoreTest
block|{
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
name|RDBDocumentStorePerformanceTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|DocumentStorePerformanceTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|RDBDocumentStorePerformanceTest
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|super
argument_list|(
name|dsf
argument_list|)
expr_stmt|;
name|assumeTrue
argument_list|(
name|ENABLED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPerfUpdateLimit
parameter_list|()
throws|throws
name|SQLException
throws|,
name|UnsupportedEncodingException
block|{
name|internalTestPerfUpdateLimit
argument_list|(
literal|"testPerfUpdateLimit"
argument_list|,
literal|"raw row update (set long)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPerfUpdateLimitString
parameter_list|()
throws|throws
name|SQLException
throws|,
name|UnsupportedEncodingException
block|{
name|internalTestPerfUpdateLimit
argument_list|(
literal|"testPerfUpdateLimitString"
argument_list|,
literal|"raw row update (set long/string)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPerfUpdateLimitStringBlob
parameter_list|()
throws|throws
name|SQLException
throws|,
name|UnsupportedEncodingException
block|{
name|internalTestPerfUpdateLimit
argument_list|(
literal|"testPerfUpdateLimitStringBlob"
argument_list|,
literal|"raw row update (set long/string/blob)"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPerfUpdateAppendString
parameter_list|()
throws|throws
name|SQLException
throws|,
name|UnsupportedEncodingException
block|{
name|internalTestPerfUpdateLimit
argument_list|(
literal|"testPerfUpdateAppendString"
argument_list|,
literal|"raw row update (append string)"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPerfUpdateGrowingDoc
parameter_list|()
throws|throws
name|SQLException
throws|,
name|UnsupportedEncodingException
block|{
name|internalTestPerfUpdateLimit
argument_list|(
literal|"testPerfUpdateGrowingDoc"
argument_list|,
literal|"raw row update (string + blob)"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|internalTestPerfUpdateLimit
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|,
name|int
name|mode
parameter_list|)
throws|throws
name|SQLException
throws|,
name|UnsupportedEncodingException
block|{
if|if
condition|(
name|super
operator|.
name|rdbDataSource
operator|!=
literal|null
condition|)
block|{
name|String
name|key
init|=
name|name
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|String
name|table
init|=
name|DocumentStoreFixture
operator|.
name|TABLEPREFIX
operator|+
literal|"NODES"
decl_stmt|;
comment|// create test node
try|try
block|{
name|connection
operator|=
name|super
operator|.
name|rdbDataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we use the same pool as the document store, and the
comment|// connection might have been returned in read-only mode
name|connection
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PreparedStatement
name|stmt
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"insert into "
operator|+
name|table
operator|+
literal|" (ID, MODCOUNT, DATA) values (?, ?, ?)"
argument_list|)
decl_stmt|;
try|try
block|{
name|setIdInStatement
argument_list|(
name|stmt
argument_list|,
literal|1
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|3
argument_list|,
literal|"X"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
comment|// ignored
comment|// ex.printStackTrace();
block|}
finally|finally
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignored
block|}
block|}
block|}
name|removeMe
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|StringBuffer
name|expect
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"X"
argument_list|)
decl_stmt|;
name|String
name|appendString
init|=
name|generateString
argument_list|(
literal|512
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|long
name|duration
init|=
literal|1000
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|duration
decl_stmt|;
name|long
name|cnt
init|=
literal|0
decl_stmt|;
name|byte
name|bdata
index|[]
init|=
operator|new
name|byte
index|[
literal|65536
index|]
decl_stmt|;
name|String
name|sdata
init|=
name|appendString
decl_stmt|;
name|boolean
name|needsConcat
init|=
name|super
operator|.
name|dsname
operator|.
name|contains
argument_list|(
literal|"MySQL"
argument_list|)
decl_stmt|;
name|boolean
name|needsSQLStringConcat
init|=
name|super
operator|.
name|dsname
operator|.
name|contains
argument_list|(
literal|"MSSql"
argument_list|)
decl_stmt|;
name|int
name|dataInChars
init|=
operator|(
operator|(
name|super
operator|.
name|dsname
operator|.
name|contains
argument_list|(
literal|"Oracle"
argument_list|)
operator|||
operator|(
name|super
operator|.
name|dsname
operator|.
name|contains
argument_list|(
literal|"MSSql"
argument_list|)
operator|)
operator|)
condition|?
literal|4000
else|:
literal|16384
operator|)
decl_stmt|;
name|int
name|dataInBytes
init|=
name|dataInChars
operator|/
literal|3
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|end
condition|)
block|{
try|try
block|{
name|connection
operator|=
name|super
operator|.
name|rdbDataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
literal|0
condition|)
block|{
name|PreparedStatement
name|stmt
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"update "
operator|+
name|table
operator|+
literal|" set MODCOUNT = ? where ID = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|stmt
argument_list|,
literal|2
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
literal|1
condition|)
block|{
name|PreparedStatement
name|stmt
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"update "
operator|+
name|table
operator|+
literal|" set MODCOUNT = ?, DATA = ? where ID = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
literal|"JSON data "
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|stmt
argument_list|,
literal|3
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
literal|2
condition|)
block|{
name|PreparedStatement
name|stmt
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"update "
operator|+
name|table
operator|+
literal|" set MODCOUNT = ?, DATA = ?, BDATA = ? where ID = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
literal|"JSON data "
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
name|bdata
index|[
operator|(
name|int
operator|)
name|cnt
operator|%
name|bdata
operator|.
name|length
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|cnt
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
literal|"JSON data "
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
literal|3
argument_list|,
name|bdata
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|stmt
argument_list|,
literal|4
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
literal|3
condition|)
block|{
name|String
name|t
init|=
literal|"update "
operator|+
name|table
operator|+
literal|" "
decl_stmt|;
name|t
operator|+=
literal|"set DATA = "
expr_stmt|;
if|if
condition|(
name|needsConcat
condition|)
block|{
name|t
operator|+=
literal|"CONCAT(DATA, ?) "
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|needsSQLStringConcat
condition|)
block|{
name|t
operator|+=
literal|"CASE WHEN LEN(DATA)<= "
operator|+
operator|(
name|dataInChars
operator|-
name|appendString
operator|.
name|length
argument_list|()
operator|)
operator|+
literal|" THEN (DATA + CAST(? AS nvarchar("
operator|+
literal|4000
operator|+
literal|"))) ELSE (DATA + CAST(DATA AS nvarchar(max))) END"
expr_stmt|;
block|}
else|else
block|{
name|t
operator|+=
literal|"DATA || CAST(? as varchar("
operator|+
name|dataInChars
operator|+
literal|"))"
expr_stmt|;
block|}
name|t
operator|+=
literal|" where ID = ?"
expr_stmt|;
name|PreparedStatement
name|stmt
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|t
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|appendString
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|stmt
argument_list|,
literal|2
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|expect
operator|.
name|append
argument_list|(
name|appendString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
comment|// ex.printStackTrace();
name|String
name|state
init|=
name|ex
operator|.
name|getSQLState
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"22001"
operator|.
name|equals
argument_list|(
name|state
argument_list|)
comment|/* everybody */
operator|||
operator|(
literal|"72000"
operator|.
name|equals
argument_list|(
name|state
argument_list|)
operator|&&
literal|1489
operator|==
name|ex
operator|.
name|getErrorCode
argument_list|()
operator|)
comment|/* Oracle */
condition|)
block|{
comment|// overflow
name|connection
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|stmt
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"update "
operator|+
name|table
operator|+
literal|" set MODCOUNT = MODCOUNT + 1, DATA = ? where ID = ?"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
literal|"X"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|stmt
argument_list|,
literal|2
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|expect
operator|=
operator|new
name|StringBuffer
argument_list|(
literal|"X"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// ex.printStackTrace();
throw|throw
operator|(
name|ex
operator|)
throw|;
block|}
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|mode
operator|==
literal|4
condition|)
block|{
name|PreparedStatement
name|stmt
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"update "
operator|+
name|table
operator|+
literal|" set MODIFIED = ?, HASBINARY = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = ?, DATA = ?, BDATA = ? where ID = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|si
init|=
literal|1
decl_stmt|;
name|stmt
operator|.
name|setObject
argument_list|(
name|si
operator|++
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|/
literal|5
argument_list|,
name|Types
operator|.
name|BIGINT
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setObject
argument_list|(
name|si
operator|++
argument_list|,
literal|0
argument_list|,
name|Types
operator|.
name|SMALLINT
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setObject
argument_list|(
name|si
operator|++
argument_list|,
name|cnt
argument_list|,
name|Types
operator|.
name|BIGINT
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setObject
argument_list|(
name|si
operator|++
argument_list|,
literal|null
argument_list|,
name|Types
operator|.
name|BIGINT
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setObject
argument_list|(
name|si
operator|++
argument_list|,
name|sdata
operator|.
name|length
argument_list|()
argument_list|,
name|Types
operator|.
name|BIGINT
argument_list|)
expr_stmt|;
if|if
condition|(
name|sdata
operator|.
name|length
argument_list|()
operator|<
name|dataInBytes
condition|)
block|{
name|stmt
operator|.
name|setString
argument_list|(
name|si
operator|++
argument_list|,
name|sdata
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBinaryStream
argument_list|(
name|si
operator|++
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stmt
operator|.
name|setString
argument_list|(
name|si
operator|++
argument_list|,
literal|"null"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
name|si
operator|++
argument_list|,
name|sdata
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setIdInStatement
argument_list|(
name|stmt
argument_list|,
name|si
operator|++
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|sdata
operator|+=
name|appendString
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|+
literal|" "
operator|+
name|ex
operator|.
name|getSQLState
argument_list|()
operator|+
literal|" "
operator|+
name|ex
operator|.
name|getErrorCode
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignored
block|}
block|}
block|}
name|cnt
operator|+=
literal|1
expr_stmt|;
block|}
comment|// check persisted values
if|if
condition|(
name|mode
operator|==
literal|3
condition|)
block|{
try|try
block|{
name|connection
operator|=
name|super
operator|.
name|rdbDataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PreparedStatement
name|stmt
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select DATA, MODCOUNT from "
operator|+
name|table
operator|+
literal|" where ID = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|setIdInStatement
argument_list|(
name|stmt
argument_list|,
literal|1
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"test record "
operator|+
name|key
operator|+
literal|" not found in "
operator|+
name|super
operator|.
name|dsname
argument_list|,
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|got
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|long
name|modc
init|=
name|rs
operator|.
name|getLong
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"column reset "
operator|+
name|modc
operator|+
literal|" times"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expect
operator|.
name|toString
argument_list|()
argument_list|,
name|got
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignored
block|}
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
name|desc
operator|+
literal|" for "
operator|+
name|super
operator|.
name|dsname
operator|+
literal|" was "
operator|+
name|cnt
operator|+
literal|" in "
operator|+
name|duration
operator|+
literal|"ms ("
operator|+
operator|(
name|cnt
operator|/
operator|(
name|duration
operator|/
literal|1000f
operator|)
operator|)
operator|+
literal|"/s)"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setIdInStatement
parameter_list|(
name|PreparedStatement
name|stmt
parameter_list|,
name|int
name|idx
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|SQLException
block|{
name|boolean
name|binaryId
init|=
name|super
operator|.
name|dsname
operator|.
name|contains
argument_list|(
literal|"MySQL"
argument_list|)
operator|||
name|super
operator|.
name|dsname
operator|.
name|contains
argument_list|(
literal|"MSSql"
argument_list|)
decl_stmt|;
if|if
condition|(
name|binaryId
condition|)
block|{
try|try
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
name|idx
argument_list|,
name|id
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"UTF-8 not supported??"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|stmt
operator|.
name|setString
argument_list|(
name|idx
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

