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
name|exporter
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
name|junit
operator|.
name|Before
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ExportOptionsTest
block|{
specifier|private
name|Options
name|options
decl_stmt|;
specifier|private
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|booleanOptionsProps
parameter_list|()
throws|throws
name|Exception
block|{
name|options
operator|.
name|parseAndConfigure
argument_list|(
name|parser
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-b"
block|}
argument_list|)
expr_stmt|;
name|ExportOptions
name|opts
init|=
name|options
operator|.
name|getOptionBean
argument_list|(
name|ExportOptions
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|opts
operator|.
name|includeBlobs
argument_list|()
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|options
operator|.
name|parseAndConfigure
argument_list|(
name|parser
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"--blobs=true"
block|}
argument_list|)
expr_stmt|;
name|opts
operator|=
name|options
operator|.
name|getOptionBean
argument_list|(
name|ExportOptions
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|opts
operator|.
name|includeBlobs
argument_list|()
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|options
operator|.
name|parseAndConfigure
argument_list|(
name|parser
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"--depth=3"
block|}
argument_list|)
expr_stmt|;
name|opts
operator|=
name|options
operator|.
name|getOptionBean
argument_list|(
name|ExportOptions
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|opts
operator|.
name|includeBlobs
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|options
operator|=
operator|new
name|Options
argument_list|()
operator|.
name|withDisableSystemExit
argument_list|()
expr_stmt|;
name|options
operator|.
name|registerOptionsFactory
argument_list|(
name|ExportOptions
operator|.
name|FACTORY
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

