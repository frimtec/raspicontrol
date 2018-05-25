package ch.frimtec.raspicontrol.libraries.mockadapter;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class I2cBusMock implements I2CBus {

    private int busNumber;

    public I2cBusMock(int busNumber) {
        this.busNumber = busNumber;
    }

    @Override
    public I2CDevice getDevice(int address) throws IOException {
        return new I2CDevice() {
            @Override
            public int getAddress() {
                return address;
            }

            @Override
            public void write(byte b) throws IOException {

            }

            @Override
            public void write(byte[] buffer, int offset, int size) throws IOException {

            }

            @Override
            public void write(byte[] buffer) throws IOException {

            }

            @Override
            public void write(int address, byte b) throws IOException {

            }

            @Override
            public void write(int address, byte[] buffer, int offset, int size) throws IOException {

            }

            @Override
            public void write(int address, byte[] buffer) throws IOException {

            }

            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public int read(byte[] buffer, int offset, int size) throws IOException {
                return 0;
            }

            @Override
            public int read(int address) throws IOException {
                return 0;
            }

            @Override
            public int read(int address, byte[] buffer, int offset, int size) throws IOException {
                return 0;
            }

            @Override
            public void ioctl(long command, int value) throws IOException {

            }

            @Override
            public void ioctl(long command, ByteBuffer data, IntBuffer offsets) throws IOException {

            }

            @Override
            public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize) throws IOException {
                return 0;
            }
        };
    }

    @Override
    public int getBusNumber() {
        return this.busNumber;
    }

    @Override
    public void close() throws IOException {

    }
}
