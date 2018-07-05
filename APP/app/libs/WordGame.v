`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/11/20 19:28:06
// Design Name: 
// Module Name: WordGame
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module WordGame
(
    input   clk, reset,ps2_clk,ps2_data,
    output reg [2:0] led,  // 编码输出端
    output reg [7:0]sel_seg,  // 数码管选择端，选择点亮某个数码管 
    output reg [6:0]seg   // 每个七段数码管的各段控制端
);
    reg [26:0]count=0;
    reg [7:0]clk_30s=0;
    reg [3:0]display=0;
    reg [4:0] state=0;
    reg [3:0]fail=0;
    wire [7:0]data;
    reg [7:0]tem=0;
    //reg [7:0]in1;
    reg clrn=1'b1;
    wire ready;
    wire overflow;     // fifo overflow
    wire [3:0] cout;  // count ps2_data bits    
    // Declare states
      parameter S0 = 0, S1 = 1, S2 = 2, S3 = 3, S4 = 4, S5 = 5;
    ps2_keyboard i1(.clk(clk),.clrn(clrn),.ps2_clk(ps2_clk),.ps2_data(ps2_data),.data(data),.ready(ready),.overflow(overflow),.count(cout));
    always@(posedge clk)
    begin
        if(reset)
            begin
                state=S0;
                count=0;
                clk_30s=0;
                fail=0;
                sel_seg=8'b11111111;
                seg=7'b1111111;
                tem=0;
             end
         else
            begin
                if((clk_30s==30||fail==4'b1010)&&state!=S3)//fail to solve
                    begin
                        state=S4;
                        if (count[15])
                            begin sel_seg=8'b11111011;seg=7'b0000011;end
                        else if(count[9])
                            begin sel_seg=8'b11111101;seg = 7'b1000000;end
                        else 
                            begin sel_seg=8'b11111110;seg = 7'b1000000;end
                    end
                 else  begin
                 if(count==100000000)
                   begin clk_30s=clk_30s+1;count=0;end
                 case(count[20])
                                      1'b0:begin  sel_seg[7:0]=8'b11111110; display=clk_30s[3:0];
                                                   case(display)
                                                       4'h0:seg = 7'b1000000;
                                                       4'h1:seg=7'b1111001;
                                                       4'h2:seg=7'b0100100;
                                                       4'h3:seg=7'b0110000;
                                                       4'h4:seg=7'b0011001;
                                                       4'h5:seg=7'b0010010;
                                                       4'h6:seg=7'b0000010;
                                                       4'h7:seg=7'b1111000;
                                                       4'h8:seg=7'b0000000;
                                                       4'h9:seg=7'b0010000;
                                                       4'ha:seg=7'b0001000;
                                                       4'hb:seg=7'b0000011;
                                                       4'hc:seg=7'b1000110;
                                                       4'hd:seg=7'b0100001;
                                                       4'he:seg=7'b0000110;
                                                       4'hf:seg=7'b0001110;
                                                       default:seg = 7'b1111111;
                                                       endcase
                                               end
                                      1'b1:begin sel_seg[7:0]=8'b11111101; display =clk_30s[7:4]; 
                                                       case(display)
                                                                         4'h0:seg = 7'b1000000;
                                                                         4'h1:seg=7'b1111001;
                                                                         4'h2:seg=7'b0100100;
                                                                         4'h3:seg=7'b0110000;
                                                                         4'h4:seg=7'b0011001;
                                                                         4'h5:seg=7'b0010010;
                                                                         4'h6:seg=7'b0000010;
                                                                         4'h7:seg=7'b1111000;
                                                                         4'h8:seg=7'b0000000;
                                                                         4'h9:seg=7'b0010000;
                                                                         4'ha:seg=7'b0001000;
                                                                         4'hb:seg=7'b0000011;
                                                                         4'hc:seg=7'b1000110;
                                                                         4'hd:seg=7'b0100001;
                                                                         4'he:seg=7'b0000110;
                                                                         4'hf:seg=7'b0001110;
                                                                         default:seg = 7'b1111111;
                                                                         endcase
                                               end
                                          
                                       endcase
                 if(tem==data)
                    ;
                 else if(data==8'hf0)
                    tem=8'hf0;
                 else if(data==8'hf0)
                 begin
                 tem=data;
                  case (state) //6:36 q:15 j:3b 6:74 6:74 6:74
                S0: if (data == 8'h36) state <= S1; else begin state <= S0; fail <= fail + 1; end
                S1: if (data == 8'h15) state <= S2; else begin if (data == 8'h11) state <= S1; else state <= S0; fail <= fail + 1; end //6
                S2: if (data == 8'h3b) state <= S3; else begin if (data == 8'h36) state <= S1; else state <= S0; fail <= fail + 1; end //6q
                S3: if (data == 8'h36) state <= S4; else begin state <= S0; fail <= fail + 1; end //6qj
                S4: if (data == 8'h36) state <= S5; else begin if (data == 8'h15) state <= S2; else state <= S0; fail = fail + 1; end //6qj6
                default: state <= S0;
                              endcase
                 end
                 end
            
             end 
         count=count+1;
    end
	
  always @ (state) begin
        case (state)
            S0: led <= 5'b00000;
            S1: led <= 5'b00001;
            S2: led <= 5'b00011;
            S3: led <= 5'b00111;
            S4: led <= 5'b01111;
            S5: led <= 5'b11111;
            default: led <= 5'b00000;
        endcase
    end
endmodule

