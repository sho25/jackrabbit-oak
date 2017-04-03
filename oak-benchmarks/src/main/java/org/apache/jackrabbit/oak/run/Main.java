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
name|run
package|;
end_package

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
name|ImmutableMap
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
name|run
operator|.
name|Command
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
name|run
operator|.
name|Modes
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
name|run
operator|.
name|Utils
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|copyOfRange
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
name|commons
operator|.
name|IOUtils
operator|.
name|closeQuietly
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|Main
block|{
specifier|private
specifier|static
specifier|final
name|Modes
name|MODES
init|=
operator|new
name|Modes
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Command
operator|>
name|of
argument_list|(
literal|"benchmark"
argument_list|,
operator|new
name|BenchmarkCommand
argument_list|()
argument_list|,
literal|"scalability"
argument_list|,
operator|new
name|ScalabilityCommand
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|Main
parameter_list|()
block|{
comment|// Prevent instantiation.
block|}
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
name|Exception
block|{
name|Utils
operator|.
name|printProductInfo
argument_list|(
name|args
argument_list|,
name|Main
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/META-INF/maven/org.apache.jackrabbit/oak-benchmarks/pom.properties"
argument_list|)
argument_list|)
expr_stmt|;
name|Command
name|c
init|=
name|MODES
operator|.
name|getCommand
argument_list|(
literal|"benchmark"
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|c
operator|=
name|MODES
operator|.
name|getCommand
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|c
operator|=
name|MODES
operator|.
name|getCommand
argument_list|(
literal|"benchmark"
argument_list|)
expr_stmt|;
block|}
name|args
operator|=
name|copyOfRange
argument_list|(
name|args
argument_list|,
literal|1
argument_list|,
name|args
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|execute
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
