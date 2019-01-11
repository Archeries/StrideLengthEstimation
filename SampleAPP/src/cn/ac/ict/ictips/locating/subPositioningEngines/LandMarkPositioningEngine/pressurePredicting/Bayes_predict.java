package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.pressurePredicting;


public class Bayes_predict {
	
	Floor_position_msg_p fps;
	Bayes_model bayesmodel;

	Bayes_predict(Floor_position_msg_p f) {

		fps=f;
		bayesmodel=f.bayesmodel;
	}

	public Bayesresult predict(int sample[], int num ) {
		double p_[][] = new double[fps.getfloornum()][num];// 用于计算样本sample中每个ap在每个楼层出现的概率
		double p2[] = new double[fps.getfloornum()];// 用于计算该样本似然函数，在每个楼层;
		for (int i = 0; i < fps.getfloornum(); i++) {
			p2[i] = 1;
			for (int j = 0; j < num; j++) {
				p_[i][j] = (double) bayesmodel.getAPcount(i,sample[j]) / (bayesmodel.getfloorsamplenum(i) + 1);// 计算每个楼层出现的概率
				if (p_[i][j] == 0.0)
					p_[i][j] = 1e-10;
				p2[i] *= p_[i][j];// 计算似然函数步骤1
			}
			p2[i] = Math.pow(p2[i], 1.0 / num);// 计算似然函数步骤2

		}

		double psum = 0;// 计算并查找最大概率的结果作为分类结果
		double maxp = -1,maxp_2nd=-1;
		int maxi = 0;
		for (int i = 0; i < fps.getfloornum(); i++) {
			double temp = p2[i] * bayesmodel.getfloorsamplenum(i) / bayesmodel.gettotalsamplenum();
			if (temp > maxp) {
				maxp_2nd=maxp;
				maxp = temp;
				maxi = i;
			}
			else if(temp > maxp_2nd){
				maxp_2nd=temp;
			}
			psum += temp;
		}
		double p = maxp / psum;// 计算出的最大概率
		double p_2nd=maxp_2nd / psum;
		//		p=p/p_2nd;

		//	System.out.println(p);
		Bayesresult rs=new Bayesresult();
		rs.floorindex = maxi;
		rs.p = p;
		rs.p2=p_2nd;
		rs.floorname = fps.getfloorname(maxi);

		return rs;
	}
}

class Bayesresult
{
	int floorindex;
	String floorname;//楼层名称
	double p,p2;//预测的概率
	Bayesresult()
	{
		floorindex=-1;
		floorname="0";
		p=-1;
		p2=-1;
	}

};
