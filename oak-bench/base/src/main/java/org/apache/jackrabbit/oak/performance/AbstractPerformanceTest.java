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
name|performance
package|;
end_package

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
name|IOException
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
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|SimpleCredentials
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
name|output
operator|.
name|FileWriterWithEncoding
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
name|math
operator|.
name|stat
operator|.
name|descriptive
operator|.
name|DescriptiveStatistics
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
name|mk
operator|.
name|MicroKernelFactory
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|mk
operator|.
name|index
operator|.
name|IndexWrapper
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
name|jcr
operator|.
name|RepositoryImpl
import|;
end_import

begin_comment
comment|/**  * This class calls all known performance tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPerformanceTest
block|{
comment|/**      * The warmup time, in ms.      */
specifier|private
specifier|final
name|int
name|warmup
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.performanceTest.warmup"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**      * How long each test is repeated, in ms.      */
specifier|private
specifier|final
name|int
name|runtime
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.performanceTest.runtime"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Credentials
name|credentials
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Pattern
name|microKernelPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"mk"
argument_list|,
literal|".*"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Pattern
name|testPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"only"
argument_list|,
literal|".*"
argument_list|)
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|testPerformance
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|microKernel
parameter_list|)
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
operator|new
name|LoginTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|LoginLogoutTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|ReadPropertyTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|SetPropertyTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|SmallFileReadTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|SmallFileWriteTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|ConcurrentReadTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|ConcurrentReadWriteTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|SimpleSearchTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|SQL2SearchTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|DescendantSearchTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|SQL2DescendantSearchTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|CreateManyChildNodesTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|UpdateManyChildNodesTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
operator|new
name|TransientManyChildNodesTest
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|runTest
parameter_list|(
name|AbstractTest
name|test
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|microKernel
parameter_list|)
block|{
if|if
condition|(
name|microKernelPattern
operator|.
name|matcher
argument_list|(
name|microKernel
argument_list|)
operator|.
name|matches
argument_list|()
operator|&&
name|testPattern
operator|.
name|matcher
argument_list|(
name|test
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|MicroKernel
name|mk
init|=
literal|null
decl_stmt|;
name|RepositoryImpl
name|repository
decl_stmt|;
try|try
block|{
name|mk
operator|=
name|createMicroKernel
argument_list|(
name|microKernel
argument_list|)
expr_stmt|;
name|repository
operator|=
name|createRepository
argument_list|(
name|mk
argument_list|)
expr_stmt|;
comment|// Run the test
name|DescriptiveStatistics
name|statistics
init|=
name|runTest
argument_list|(
name|test
argument_list|,
name|repository
argument_list|)
decl_stmt|;
if|if
condition|(
name|statistics
operator|.
name|getN
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writeReport
argument_list|(
name|test
operator|.
name|toString
argument_list|()
argument_list|,
name|name
argument_list|,
name|microKernel
argument_list|,
name|statistics
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|re
parameter_list|)
block|{
name|re
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|mk
operator|!=
literal|null
condition|)
block|{
name|MicroKernelFactory
operator|.
name|disposeInstance
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|DescriptiveStatistics
name|runTest
parameter_list|(
name|AbstractTest
name|test
parameter_list|,
name|Repository
name|repository
parameter_list|)
throws|throws
name|Exception
block|{
name|DescriptiveStatistics
name|statistics
init|=
operator|new
name|DescriptiveStatistics
argument_list|()
decl_stmt|;
name|test
operator|.
name|setUp
argument_list|(
name|repository
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Run a few iterations to warm up the system
if|if
condition|(
name|warmup
operator|>
literal|0
condition|)
block|{
name|long
name|warmupEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|warmup
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|warmupEnd
condition|)
block|{
name|test
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Run test iterations, and capture the execution times
name|long
name|runtimeEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|runtime
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|runtimeEnd
condition|)
block|{
name|statistics
operator|.
name|addValue
argument_list|(
name|test
operator|.
name|execute
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|test
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
return|return
name|statistics
return|;
block|}
specifier|private
specifier|static
name|void
name|writeReport
parameter_list|(
name|String
name|test
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|microKernel
parameter_list|,
name|DescriptiveStatistics
name|statistics
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|report
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|test
operator|+
literal|"-"
operator|+
name|microKernel
operator|+
literal|".txt"
argument_list|)
decl_stmt|;
name|boolean
name|needsPrefix
init|=
operator|!
name|report
operator|.
name|exists
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriterWithEncoding
argument_list|(
name|report
argument_list|,
literal|"UTF-8"
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|needsPrefix
condition|)
block|{
name|writer
operator|.
name|format
argument_list|(
literal|"# %-34.34s     min     10%%     50%%     90%%     max%n"
argument_list|,
name|test
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|format
argument_list|(
literal|"%-36.36s  %6.0f  %6.0f  %6.0f  %6.0f  %6.0f%n"
argument_list|,
name|name
argument_list|,
name|statistics
operator|.
name|getMin
argument_list|()
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|10.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|50.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getPercentile
argument_list|(
literal|90.0
argument_list|)
argument_list|,
name|statistics
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|MicroKernel
name|createMicroKernel
parameter_list|(
name|String
name|microKernel
parameter_list|)
block|{
comment|// TODO: depending on the microKernel string a particular repository
comment|// with that MK must be returned
return|return
operator|new
name|MicroKernelImpl
argument_list|(
literal|"target/mk-tck-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|RepositoryImpl
name|createRepository
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
comment|// return new RepositoryImpl();
name|mk
operator|=
operator|new
name|IndexWrapper
argument_list|(
name|mk
argument_list|)
expr_stmt|;
name|ContentRepository
name|contentRepository
init|=
operator|new
name|Oak
argument_list|(
name|mk
argument_list|)
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
return|return
operator|new
name|RepositoryImpl
argument_list|(
name|contentRepository
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

