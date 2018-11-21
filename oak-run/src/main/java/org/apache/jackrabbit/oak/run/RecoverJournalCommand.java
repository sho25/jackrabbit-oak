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
name|run
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|RecoverJournal
import|;
end_import

begin_class
class|class
name|RecoverJournalCommand
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
name|options
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|help
init|=
name|options
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"Prints help and exits"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|dir
init|=
name|options
operator|.
name|nonOptions
argument_list|()
operator|.
name|describedAs
argument_list|(
literal|"path"
argument_list|)
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSet
name|parsed
init|=
name|options
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|parsed
operator|.
name|has
argument_list|(
name|help
argument_list|)
condition|)
block|{
name|options
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
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
if|if
condition|(
name|parsed
operator|.
name|valuesOf
argument_list|(
name|dir
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Segment Store path not specified"
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
if|if
condition|(
name|parsed
operator|.
name|valuesOf
argument_list|(
name|dir
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Too many Segment Store paths specified"
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
name|int
name|code
init|=
name|RecoverJournal
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|dir
operator|.
name|value
argument_list|(
name|parsed
argument_list|)
argument_list|)
operator|.
name|withOut
argument_list|(
name|System
operator|.
name|out
argument_list|)
operator|.
name|withErr
argument_list|(
name|System
operator|.
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
name|code
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

