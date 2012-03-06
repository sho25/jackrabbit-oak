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
name|mk
package|;
end_package

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
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|ParseException
import|;
end_import

begin_comment
comment|/**  * A simple hello world app.  */
end_comment

begin_class
specifier|public
class|class
name|HelloWorld
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|ParseException
block|{
name|test
argument_list|(
literal|"fs:{homeDir};clean"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"simple:"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"simple:fs:target/temp;clean"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|test
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|ParseException
block|{
name|MicroKernel
name|mk
init|=
name|MicroKernelFactory
operator|.
name|getInstance
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"hello\" : {}"
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|move
init|=
literal|"> \"hello\": \"world\" "
decl_stmt|;
name|String
name|set
init|=
literal|"^ \"world/x\": 1 "
decl_stmt|;
try|try
block|{
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|move
operator|+
name|set
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"move& set worked"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"move& set didn't work:"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|move
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|set
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

