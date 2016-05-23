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
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

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

begin_class
class|class
name|GraphCommand
implements|implements
name|Command
block|{
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
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|directoryArg
init|=
name|parser
operator|.
name|nonOptions
argument_list|(
literal|"Path to segment store (required)"
argument_list|)
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|outFileArg
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"output"
argument_list|,
literal|"Output file"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
operator|new
name|File
argument_list|(
literal|"segments.gdf"
argument_list|)
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Long
argument_list|>
name|epochArg
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"epoch"
argument_list|,
literal|"Epoch of the segment time stamps (derived from journal.log if not given)"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|gcGraphArg
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"gc"
argument_list|,
literal|"Write the gc generation graph instead of the full graph"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|regExpArg
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"pattern"
argument_list|,
literal|"Regular exception specifying which nodes to include (optional). "
operator|+
literal|"Ignore when --gc is specified."
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
name|segmentTar
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"segment-tar"
argument_list|,
literal|"Use oak-segment-tar instead of oak-segment"
argument_list|)
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|File
name|directory
init|=
name|directoryArg
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Dump the segment graph to a file. Usage: graph [File]<options>"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|regExp
init|=
name|regExpArg
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|File
name|outFile
init|=
name|outFileArg
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|Date
name|epoch
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|epochArg
argument_list|)
condition|)
block|{
name|epoch
operator|=
operator|new
name|Date
argument_list|(
name|epochArg
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Calendar
name|c
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|c
operator|.
name|setTimeInMillis
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"journal.log"
argument_list|)
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|epoch
operator|=
name|c
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|outFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|outFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Setting epoch to "
operator|+
name|epoch
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Writing graph to "
operator|+
name|outFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|outFile
argument_list|)
decl_stmt|;
name|boolean
name|gcGraph
init|=
name|options
operator|.
name|has
argument_list|(
name|gcGraphArg
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|segmentTar
argument_list|)
condition|)
block|{
name|SegmentTarUtils
operator|.
name|graph
argument_list|(
name|directory
argument_list|,
name|gcGraph
argument_list|,
name|epoch
argument_list|,
name|regExp
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SegmentUtils
operator|.
name|graph
argument_list|(
name|directory
argument_list|,
name|gcGraph
argument_list|,
name|epoch
argument_list|,
name|regExp
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

