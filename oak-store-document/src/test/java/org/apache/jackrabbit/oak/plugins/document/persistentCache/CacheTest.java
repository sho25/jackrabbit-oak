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
name|plugins
operator|.
name|document
operator|.
name|persistentCache
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
name|FileOutputStream
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
name|cache
operator|.
name|Cache
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
name|FileUtils
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
name|cache
operator|.
name|CacheLIRS
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
name|junit
operator|.
name|LogCustomizer
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
name|PathRev
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
name|Revision
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
name|RevisionVector
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
name|util
operator|.
name|StringValue
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
name|event
operator|.
name|Level
import|;
end_import

begin_class
specifier|public
class|class
name|CacheTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|recoverIfCorrupt
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|expectedWarning
init|=
literal|"Too many re-opens"
decl_stmt|;
name|LogCustomizer
name|lc
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|CacheMap
operator|.
name|class
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
operator|.
name|contains
argument_list|(
name|expectedWarning
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
name|lc
operator|.
name|starting
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest"
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/cacheTest"
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
literal|"target/cacheTest/cache-0.data"
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"corrupt"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|PersistentCache
name|pCache
init|=
operator|new
name|PersistentCache
argument_list|(
literal|"target/cacheTest"
argument_list|)
decl_stmt|;
name|CacheLIRS
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
name|cache
init|=
operator|new
name|CacheLIRS
operator|.
name|Builder
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
argument_list|()
operator|.
name|maximumSize
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Cache
argument_list|<
name|PathRev
argument_list|,
name|StringValue
argument_list|>
name|map
init|=
name|pCache
operator|.
name|wrap
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|cache
argument_list|,
name|CacheType
operator|.
name|DIFF
argument_list|)
decl_stmt|;
name|String
name|largeString
init|=
operator|new
name|String
argument_list|(
operator|new
name|char
index|[
literal|1024
operator|*
literal|1024
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|counter
init|=
literal|0
init|;
name|counter
operator|<
literal|10
condition|;
name|counter
operator|++
control|)
block|{
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|100
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
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|PathRev
name|k
init|=
operator|new
name|PathRev
argument_list|(
literal|"/"
operator|+
name|counter
argument_list|,
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|map
operator|.
name|getIfPresent
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|k
argument_list|,
operator|new
name|StringValue
argument_list|(
name|largeString
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Exceptions: "
operator|+
name|pCache
operator|.
name|getExceptionCount
argument_list|()
argument_list|,
name|pCache
operator|.
name|getExceptionCount
argument_list|()
operator|<
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"WARN level log should contain one entry containing '"
operator|+
name|expectedWarning
operator|+
literal|"'"
argument_list|,
name|lc
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lc
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|closeAlways
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest"
argument_list|)
argument_list|)
expr_stmt|;
name|PersistentCache
name|cache
init|=
operator|new
name|PersistentCache
argument_list|(
literal|"target/cacheTest,manualCommit"
argument_list|)
decl_stmt|;
name|CacheMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|cache
operator|.
name|openMap
argument_list|(
literal|0
argument_list|,
literal|"test"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// break the map by calling interrupt
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"hello"
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|Thread
operator|.
name|interrupted
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteOldAtStartup
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest"
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/cacheTest"
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/cacheTest/cache-0.data"
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/cacheTest/cache-1.data"
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/cacheTest/cache-2.data"
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/cacheTest/cache-3.data"
argument_list|)
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|PersistentCache
name|cache
init|=
operator|new
name|PersistentCache
argument_list|(
literal|"target/cacheTest"
argument_list|)
decl_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest/cache-0.data"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest/cache-1.data"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest/cache-2.data"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest/cache-3.data"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|interrupt
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/cacheTest"
argument_list|)
argument_list|)
expr_stmt|;
name|PersistentCache
name|cache
init|=
operator|new
name|PersistentCache
argument_list|(
literal|"target/cacheTest,size=1,-compress"
argument_list|)
decl_stmt|;
try|try
block|{
name|CacheMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m1
init|=
name|cache
operator|.
name|openMap
argument_list|(
literal|0
argument_list|,
literal|"m1"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CacheMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m2
init|=
name|cache
operator|.
name|openMap
argument_list|(
literal|0
argument_list|,
literal|"test"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// the cache file was opened once so far
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cache
operator|.
name|getOpenCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// we store 20 mb of data, to ensure not all data is kept in memory
name|String
name|largeString
init|=
operator|new
name|String
argument_list|(
operator|new
name|char
index|[
literal|1024
operator|*
literal|1024
index|]
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|10
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|m1
operator|.
name|put
argument_list|(
literal|"x"
operator|+
name|i
argument_list|,
name|largeString
argument_list|)
expr_stmt|;
name|m2
operator|.
name|put
argument_list|(
literal|"y"
operator|+
name|i
argument_list|,
name|largeString
argument_list|)
expr_stmt|;
block|}
comment|// interrupt the thread, which will cause the FileChannel
comment|// to be closed in the next read operation
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// this will force at least one read operation,
comment|// which should re-open the maps
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|m1
operator|.
name|get
argument_list|(
literal|"x"
operator|+
name|i
argument_list|)
expr_stmt|;
name|m2
operator|.
name|get
argument_list|(
literal|"y"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cache
operator|.
name|getOpenCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// re-opening will clear the interrupt flag
name|assertFalse
argument_list|(
name|Thread
operator|.
name|interrupted
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

