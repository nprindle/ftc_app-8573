package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtGyroSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gyroscope;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.teamcode.MathUtils;
import org.firstinspires.ftc.teamcode.RobotUtils;


@TeleOp(name = "GyroSensor", group = "TeleOp")

public class SensorTest extends OpMode {
    
    private DcMotor firstFlip;
    Gyroscope              gyroscope;
    HiTechnicNxtGyroSensor hiTechnicNxtGyroSensor;
    double   degrees  = 0.0;
    double[] velocDeg = new double[2];

    public void init() {
        gyroscope = hardwareMap.get(Gyroscope.class, "gyro");
        hiTechnicNxtGyroSensor = hardwareMap.get(HiTechnicNxtGyroSensor.class, "gyro");
    }

    @Override
    public void init_loop() {
        firstFlip = RobotUtils.registerMotor(hardwareMap, "firstFlip", true, "encoder");
    }

    // simply ensures motors are stopped when start is pressed
    @Override
    public void start() {
        hiTechnicNxtGyroSensor.calibrate();
    }

    // ensures that motors are halted when telemetry ends
    // otherwise, would run continually for several seconds
    @Override
    public void stop() {
        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }

    public void loop() {
       float left_y_2 = -gamepad2.left_stick_y;
        if (Math.abs(left_y_2) > 0.1) {
                firstFlip.setPower(left_y_2*0.5);
                telemetry.addData("encoder position for first flip", firstFlip.getCurrentPosition());
            } else {
                firstFlip.setPower(0);
                telemetry.addData("encoder position for first flip", firstFlip.getCurrentPosition());
            }
    }

    public void isTurning() {
        double          raw      = hiTechnicNxtGyroSensor.readRawVoltage();
        double          bias     = hiTechnicNxtGyroSensor.getBiasVoltage();
        double          current  = System.nanoTime();
        AngularVelocity velocity = hiTechnicNxtGyroSensor.getAngularVelocity(AngleUnit.DEGREES);
        int             adjVeloc = Math.abs(velocity.zRotationRate) < 1 ? 0 : (int) velocity
                .zRotationRate;
        degrees += ((velocity.acquisitionTime - current) * velocity.zRotationRate / 1000000000);
        telemetry.addData("rate", "%.4f deg/s", adjVeloc);
        telemetry.addData("deg", "%.4f deg", degrees * 180);
        if ((Math.abs(degrees * 180)) > 89 && (Math.abs(degrees * 180)) < 91) {
            telemetry.addData("IN THE IF", degrees * 180);
        }
        telemetry.update();

    }


    public double avgVelocity() {
        double[] vs      = new double[5];
        double   average = 0.0;
        double   result  = 0.0;
        int      count   = 0;
        for (int i = 0; i < 5; i++) {
            vs[i] = hiTechnicNxtGyroSensor.getAngularVelocity(AngleUnit.DEGREES).zRotationRate;
            average += vs[i] / 5;
        }
        double stdev = 0;
        for (double x : vs) {
            stdev += Math.abs(x - average) / 4;
        }
        for (double x : vs) {
            if (x - average < 100 * stdev)
                count++;
            result += x;
        }
        return (average);
    }
    

}
