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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|FileStoreHelper
operator|.
name|isValidFileStoreOrFail
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
name|LinkedHashSet
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
name|joptsimple
operator|.
name|ArgumentAcceptingOptionSpec
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|tool
operator|.
name|Check
import|;
end_import

begin_class
class|class
name|CheckCommand
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
name|ArgumentAcceptingOptionSpec
argument_list|<
name|String
argument_list|>
name|journal
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"journal"
argument_list|,
literal|"journal file"
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
operator|.
name|defaultsTo
argument_list|(
literal|"journal.log"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|deep
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"deep"
argument_list|,
literal|"<deprecated> enable deep consistency checking."
argument_list|)
decl_stmt|;
name|ArgumentAcceptingOptionSpec
argument_list|<
name|Long
argument_list|>
name|notify
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"notify"
argument_list|,
literal|"number of seconds between progress notifications"
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
operator|.
name|defaultsTo
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|bin
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"bin"
argument_list|,
literal|"read the content of binary properties"
argument_list|)
decl_stmt|;
name|ArgumentAcceptingOptionSpec
argument_list|<
name|String
argument_list|>
name|filter
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"filter"
argument_list|,
literal|"comma separated content paths to be checked"
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
operator|.
name|withValuesSeparatedBy
argument_list|(
literal|','
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|head
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"head"
argument_list|,
literal|"checks only latest /root (i.e without checkpoints)"
argument_list|)
decl_stmt|;
name|ArgumentAcceptingOptionSpec
argument_list|<
name|String
argument_list|>
name|cp
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"checkpoints"
argument_list|,
literal|"checks only specified checkpoints (comma separated); use --checkpoints all to check all checkpoints"
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
literal|','
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|"all"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|ioStatistics
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"io-stats"
argument_list|,
literal|"Print I/O statistics (only for oak-segment-tar)"
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
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|out
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PrintWriter
name|err
init|=
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|err
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|nonOptionArguments
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|printUsage
argument_list|(
name|parser
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
name|File
name|dir
init|=
name|isValidFileStoreOrFail
argument_list|(
operator|new
name|File
argument_list|(
name|options
operator|.
name|nonOptionArguments
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|journalFileName
init|=
name|journal
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|long
name|debugLevel
init|=
name|notify
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|filterPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|filter
operator|.
name|values
argument_list|(
name|options
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|checkpoints
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|cp
argument_list|)
operator|||
operator|!
name|options
operator|.
name|has
argument_list|(
name|head
argument_list|)
condition|)
block|{
name|checkpoints
operator|.
name|addAll
argument_list|(
name|cp
operator|.
name|values
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|deep
argument_list|)
condition|)
block|{
name|printUsage
argument_list|(
name|parser
argument_list|,
name|err
argument_list|,
literal|"The --deep option was deprecated! Please do not use it in the future!"
argument_list|,
literal|"A deep scan of the content tree, traversing every node, will be performed by default."
argument_list|)
expr_stmt|;
block|}
name|boolean
name|checkHead
init|=
operator|!
name|options
operator|.
name|has
argument_list|(
name|cp
argument_list|)
operator|||
name|options
operator|.
name|has
argument_list|(
name|head
argument_list|)
decl_stmt|;
name|int
name|statusCode
init|=
name|Check
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|dir
argument_list|)
operator|.
name|withJournal
argument_list|(
name|journalFileName
argument_list|)
operator|.
name|withDebugInterval
argument_list|(
name|debugLevel
argument_list|)
operator|.
name|withCheckBinaries
argument_list|(
name|options
operator|.
name|has
argument_list|(
name|bin
argument_list|)
argument_list|)
operator|.
name|withCheckHead
argument_list|(
name|checkHead
argument_list|)
operator|.
name|withCheckpoints
argument_list|(
name|checkpoints
argument_list|)
operator|.
name|withFilterPaths
argument_list|(
name|filterPaths
argument_list|)
operator|.
name|withIOStatistics
argument_list|(
name|options
operator|.
name|has
argument_list|(
name|ioStatistics
argument_list|)
argument_list|)
operator|.
name|withOutWriter
argument_list|(
name|out
argument_list|)
operator|.
name|withErrWriter
argument_list|(
name|err
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|statusCode
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|printUsage
parameter_list|(
name|OptionParser
name|parser
parameter_list|,
name|PrintWriter
name|err
parameter_list|,
name|String
modifier|...
name|messages
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|message
range|:
name|messages
control|)
block|{
name|err
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|err
operator|.
name|println
argument_list|(
literal|"usage: check path/to/segmentstore<options>"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|printHelpOn
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

