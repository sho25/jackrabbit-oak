begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|segment
operator|.
name|file
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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
import|;
end_import

begin_comment
comment|/**  * An amount of bytes that is also pretty-printable for usage in logs.  */
end_comment

begin_class
class|class
name|PrintableBytes
block|{
comment|/**      * Create a new instance a {@link PrintableBytes}.      *      * @param bytes The amount of bytes.      * @return A new instance of {@link PrintableBytes}.      */
specifier|static
name|PrintableBytes
name|newPrintableBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
return|return
operator|new
name|PrintableBytes
argument_list|(
name|bytes
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|long
name|bytes
decl_stmt|;
specifier|private
name|PrintableBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%d bytes)"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

