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
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
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
name|indexversion
operator|.
name|PurgeOldIndexVersion
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
name|run
operator|.
name|cli
operator|.
name|Options
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
name|run
operator|.
name|commons
operator|.
name|Command
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
specifier|public
class|class
name|PurgeOldIndexVersionCommand
implements|implements
name|Command
block|{
specifier|private
name|long
name|threshold
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|indexPaths
decl_stmt|;
specifier|private
name|long
name|DEFAULT_PURGE_THRESHOLD
init|=
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|// 5 days in millis
specifier|private
specifier|final
specifier|static
name|String
name|DEFAULT_INDEX_PATH
init|=
literal|"/oak:index"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Options
name|opts
init|=
name|parseCommandLineParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
operator|new
name|PurgeOldIndexVersion
argument_list|()
operator|.
name|execute
argument_list|(
name|opts
argument_list|,
name|threshold
argument_list|,
name|indexPaths
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Options
name|parseCommandLineParams
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|Long
argument_list|>
name|thresholdOption
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"threshold"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Long
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
name|DEFAULT_PURGE_THRESHOLD
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|indexPathsOption
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"index-paths"
argument_list|,
literal|"Comma separated list of index paths for which the "
operator|+
literal|"selected operations need to be performed"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|withValuesSeparatedBy
argument_list|(
literal|","
argument_list|)
operator|.
name|defaultsTo
argument_list|(
name|DEFAULT_INDEX_PATH
argument_list|)
decl_stmt|;
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|OptionSet
name|optionSet
init|=
name|opts
operator|.
name|parseAndConfigure
argument_list|(
name|parser
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|this
operator|.
name|threshold
operator|=
name|optionSet
operator|.
name|valueOf
argument_list|(
name|thresholdOption
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexPaths
operator|=
name|optionSet
operator|.
name|valuesOf
argument_list|(
name|indexPathsOption
argument_list|)
expr_stmt|;
return|return
name|opts
return|;
block|}
block|}
end_class

end_unit
